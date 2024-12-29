# ECommerceBackendSpringboot
CODE for the Project : https://www.youtube.com/watch?v=oGhc5Z-WJSw

## What we learn 

Entity Concepts 

Service Concepts 

Repository Concepts 

Controller Concepts 

Components 

Beans 

Confguration 

Application Properties 

Spring Security 


## Some Questions 


1. What is DispatcherServlet Initialization.?
2. What is  lazy loading.?
3. What Logging Enhancements and what are different properties in application.properties that can help in loggigng
   like
   ```
   spring.jpa.properties.hibernate.generate_statistics=true
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   ```
   
5. What is CASCADE and its different types like CascadeType.ALL?
6. Waht is @OneToMany and other annotations like @oneToOne, @ManyToOne what are its other properties like cascade , orphanRemoval etc.
7. Expain these annotations
   @Transactional,
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @JoinColumn(name = "user_id") @JoinColumn(name = "cart_id", nullable = false)
   @Entity    





7.Expalin  Explicit Query: Use a custom repository method for deletion:
```
@Modifying
@Query("DELETE FROM Cart c WHERE c.id = :id")
void deleteCartById(@Param("id") Long id);
```

8.Why orphanRemoval = true Doesn’t Work on @ManyToOne?

Answer : In a @ManyToOne relationship, the same child entity can be referenced by multiple parents. Removing one reference does not imply that the child entity is no longer needed.
Automatic orphan removal could lead to unintended deletions when other entities still reference the child. 



## Issues Faced and learnings 

### Issue 1. Table for the Entity Class order was not getting created . 
Learning : ORDER is reserved word in MySQL 

###  Issue 2. When Order is created the Cart row was not getting deleted. 
#### Learning : AS order table have a foregin key constraints on the user table with the User_id and Cart_id , the JPA was not executing the delete command for the Cart table. 
Moreover it siligently suppressing the issue . 

#### Why JPA Doesn’t Show Errors
JPA assumes that the developer ensures consistency between the object model and the database schema. If the schema prevents the deletion of the Cart due to foreign key constraints, the database will silently reject it without throwing a direct exception unless the transaction is explicitly rolled back.
If the user table is referencing the cart table via a foreign key constraint and cart_id is not nullable, it could prevent deletion of the cart row if there’s a violation of referential integrity. However, in JPA, the absence of an error might indicate that the framework isn’t actively managing or validating the cascade settings between User and Cart entities.

#### Possible Reasons for Missing DELETE Command
#### 1. Persistence Context Mismanagement

If the Cart entity instance passed to delete(cart) is not managed by the current Hibernate EntityManager, no SQL DELETE operation will be issued.
This can happen if:
The Cart object was not fetched from the database in the same transaction.
The Cart object is detached or stale.

#### 2. Soft Deletion Mechanism

If your application uses a "soft delete" mechanism (e.g., marking an entity as deleted rather than actually deleting it), the delete(cart) method might only update the entity with a "deleted" flag.
Check for annotations like @SQLDelete or @Where on the Cart entity.

#### 3.Cascading Issues

If the Cart has relationships with CartItem and the cascade type is misconfigured, Hibernate might skip deletion to avoid breaking foreign key constraints.
Transactional Rollback

If a transaction is rolled back before it commits, the DELETE operation won't appear in the logs or database.

#### 4.Custom Repository Logic

If you’ve overridden the delete method in a custom implementation of the repository, it may not actually delete the entity.


#### Debugging : 
Check Database Logs : Enable SQL query logging to see the actual SQL queries being executed by JPA. In application.properties or application.yml, add:

```
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE

```
Look for the following:
Whether an UPDATE user query is issued to nullify cart_id.
Whether the DELETE cart query is executed after the update.


 #### Confirming the Issue : 
 Run the following queries to confirm the relationship between user and cart:

Check user.cart_id values:

```
SELECT id, cart_id FROM user WHERE cart_id = <cart_id_to_delete>;
```

Foreign Key Constraints in Database : 
If there’s a database-level foreign key constraint on CartItem or User, the DELETE operation might silently fail.
Run this query on the database to check constraints:

```
SELECT * FROM information_schema.table_constraints WHERE table_name = 'cart';
```


Verify constraints:

```
SELECT TABLE_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE REFERENCED_TABLE_NAME = 'cart';
```



#### Solution : Force Synchronization with entityManager.flush()
Use the EntityManager to force synchronization of the User entity before deleting the Cart:
```
@Autowired
private EntityManager entityManager;

@Transactional
@Override
public void clearCartById(Long id) {
    Cart cart = cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found" + id));

    User user = cart.getUser();
    if (user != null) {
        user.setCart(null); // Nullify the cart reference in the user
        entityManager.flush(); // Force synchronization
    }
    
    cartRepository.delete(cart);
    cartRepository.flush();
}

```







### Issue 3 : @Value annotation is not setting the value from the application.properties file 

Learning : The issue of @Value("${auth.token.jwtSecret}") being null in a private method indicates that the jwtSecret value from your application properties is not being properly injected into the class. This can happen due to a few common misconfigurations or lifecycle issues. Below are the steps to resolve the problem
1. Annotate Class with @Component or Similar Annotation
2. Use @PostConstruct to Initialize

   ```
   @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.jwtSecret));
    }
   ```
   
### Issue 4 : io.jsonwebtoken.security.WeakKeyException:
Learning : The error indicates that the provided key is insufficient for the HS512 algorithm, which requires a key size of at least 512 bits (64 bytes). Here's how you can resolve the issue by providing a sufficiently secure key.Do not share this key publicly. Treat it as a sensitive secret.
Use environment variables or a secure vault to store your JWT secret in production environments.
If you need a longer or different secret, you can use the following command to generate a Base64-encoded key:
```
openssl rand -base64 32
```
The error indicates that the key being used for signing or verifying a JWT is too weak and does not meet the security requirements for HMAC-SHA algorithms. Specifically, it is only 64 bits (8 bytes), whereas HMAC-SHA256 and stronger algorithms require a key of at least 256 bits (32 bytes).


### Issue 5 : Geeting this error in DataInitilizer class org.springframework.dao.InvalidDataAccessApiUsageException: detached entity passed to persist: com.avi.eCommerce.model.Role
Learning : You're trying to persist a Role entity that isn't managed by the current Hibernate session.
This often occurs when entities are fetched in one transaction and used in another without reattaching them.
Adding @Transactional annotation at class level resolves this issue. 

### Issue 6 : HikariPool-1 - Thread starvation or clock leap detected (housekeeper delta=59m6s446ms).
Learning : The error message HikariPool-1 - Thread starvation or clock leap detected (housekeeper delta=59m6s446ms) indicates that HikariCP's internal housekeeper thread detected an unusually long delay between its scheduled executions. This often points to a problem with the application's environment or configuration. Here's how to troubleshoot and resolve the issue:

Possible Causes and Solutions
1. Thread Starvation
Cause: Your application's thread pool is insufficient, or other processes are monopolizing CPU resources.
Solution:
Increase the available threads in your application’s thread pool if it's configurable (e.g., in frameworks like Spring Boot, the thread pool size can be configured in application.properties or application.yml).
Ensure there are enough CPU resources on the server or system hosting your application. Check CPU usage and see if another process is consuming most of the CPU.
```
top
```

If you're using a containerized environment (e.g., Docker or Kubernetes), make sure sufficient CPU and memory resources are allocated.
2. Clock Leap
Cause: A significant time adjustment occurred on the system clock (e.g., due to NTP synchronization or a virtual machine's clock being out of sync).
Solution:
Synchronize System Clock: Ensure the system clock is synchronized using NTP (Network Time Protocol). On most systems, you can verify and enable NTP:
```
sudo systemctl enable --now ntp
sudo ntpdate -q pool.ntp.org
```
Virtual Machine Consideration: If running in a virtualized environment, ensure the host machine's clock is synchronized. Sometimes VMs experience clock drift, especially if CPU resources are overcommitted.
Disable Aggressive NTP Updates: If frequent clock adjustments are unavoidable, consider running your application on a system with minimal clock changes.
3. Long Garbage Collection (GC) Pauses
Cause: A Full GC or long GC pause could delay the housekeeper thread execution.
Solution:
Optimize JVM GC Settings:
If using Java 8+, consider switching to the G1 garbage collector:
```
-XX:+UseG1GC
```
If already using G1GC, fine-tune the settings, such as -XX:MaxGCPauseMillis.
Monitor GC Logs: Enable GC logging to identify long pauses:
```
-Xlog:gc*,gc+cpu,gc+heap::time,uptime,level:file=gc.log:tags,uptime,time,level
```
 Increase JVM Heap Size: Ensure the JVM has sufficient memory to avoid frequent GCs:
```
-Xmx2g -Xms2g
```
4. Database Connection Pool Misconfiguration
Cause: HikariCP may not have enough connections in the pool, or connections are being exhausted by long-running queries.
Solution:
Increase Connection Pool Size: Check your HikariCP configuration and ensure the maximumPoolSize is appropriate for your workload:
properties
```
spring.datasource.hikari.maximum-pool-size=10
```
Monitor Database Queries: Use database monitoring tools to identify long-running or slow queries and optimize them.
Connection Timeout: Adjust the connection timeout in the HikariCP configuration:
properties
```
spring.datasource.hikari.connection-timeout=30000
```
5. Application Overload
Cause: Your application might be experiencing higher-than-expected traffic, leading to resource contention.
Solution:
Scale your application horizontally by adding more instances if the system is under heavy load.
Use monitoring tools like Prometheus, Grafana, or APM tools (New Relic, Dynatrace) to analyze traffic and resource usage.

### Issue 7 : Failed to Connect to MySQL at localhost:3306 with user root
Learning : for me changing the URL for  ```localhost:3306``` to ```127.0.0.1:3306``` worked.

If you're encountering the error Failed to Connect to MySQL at localhost:3306 with user root on your Mac, here are steps you can follow to troubleshoot and resolve the issue:

1. Check if MySQL Server is Running:
First, check if the MySQL server is running on your Mac. Open Terminal and run:
```
ps aux | grep mysqld
```
If MySQL is not running, you can start it using the following command (assuming you installed MySQL via Homebrew):
```
brew services start mysql
```
If you're using another method to manage MySQL (e.g., using MySQL Workbench or the native installer), start the MySQL service accordingly.

2. Check MySQL Listening Port:
Ensure that MySQL is listening on port 3306. In Terminal, run:
``` sudo lsof -i :3306 ```
This command will show whether MySQL is bound to port 3306. If it's not, you might need to check the MySQL configuration.

3. Test MySQL Connection Locally:
Test the connection to MySQL using the MySQL command line client:
``` mysql -u root -p ```
If you can’t connect, MySQL may not be accepting connections from localhost for the root user.

4. Check MySQL Root User Password:
If you can’t connect to MySQL as root, it's possible that the password is incorrect or hasn't been set.
To reset the password, you can follow these steps:
Stop the MySQL server:
``` brew services stop mysql ```
Start MySQL in safe mode (this allows you to bypass authentication):

```sudo mysqld_safe --skip-grant-tables ```
In a new terminal window, log in to MySQL:

``` mysql -u root ```
Update the root password:

```USE mysql;
UPDATE user SET authentication_string=PASSWORD('new_password') WHERE User='root';
FLUSH PRIVILEGES;
```
Restart MySQL:

```brew services restart mysql```

5. Check MySQL Configuration (my.cnf):
Ensure that MySQL is configured to allow connections on localhost:
Open /etc/my.cnf or /usr/local/etc/my.cnf (or wherever your my.cnf file is located).
Look for the bind-address setting:
ini

```bind-address = 127.0.0.1```
If it’s set to a different address, change it to 127.0.0.1 or 0.0.0.0 to allow local connections.
Restart MySQL after making changes:

```brew services restart mysql```

6. Check MySQL Logs:
Check MySQL logs for any relevant error messages. You can view the logs by running:

```tail -f /usr/local/var/mysql/your_hostname.err```
This might give you more detailed information about why the connection is failing.

7. Check Firewall or Security Software:
Ensure that no firewall or security software (like Little Snitch) is blocking the MySQL port (3306).

8. Verify JDBC Configuration:
If you're connecting from an application (e.g., Spring, Hibernate), verify that the JDBC connection settings are correct:
properties
```
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=root
spring.datasource.password=your_password
Double-check that you're using the correct username (root) and password.
```

