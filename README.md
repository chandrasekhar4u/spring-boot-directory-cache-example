# spring-boot-directory-cache-example
Spring Watch Directory and Cache Example

## Changes are in Progress...

## cache fetches user details.. if repetative call then it will take data from cache.
2017-04-24 02:13:14.955  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : .... Fetching user details
2017-04-24 02:13:19.966  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : User 001 -->UserDetails [userId=001, firstName=Name001]
2017-04-24 02:13:19.967  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : User 001 -->UserDetails [userId=001, firstName=Name001]
2017-04-24 02:13:19.968  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : User 001 -->UserDetails [userId=001, firstName=Name001]
2017-04-24 02:13:24.968  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : User 002 -->UserDetails [userId=002, firstName=Name002]
## below data fetched from cache......
2017-04-24 02:13:24.968  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : User 002 -->UserDetails [userId=002, firstName=Name002]
2017-04-24 02:13:24.969  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : User 002 -->UserDetails [userId=002, firstName=Name002]
2017-04-24 02:13:24.972  INFO 7016 --- [           main] c.educatejava.spring.cache.Application   : Started Application in 11.697 seconds (JVM running for 12.128)
## Folder/file watcher 
## below notification is for modification of the file....
2017-04-24 02:13:32.240  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : Detected file create/change event at: C:\temp\New folder\sssdd.txt
2017-04-24 02:13:46.761  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : Detected file create/change event at: C:\temp\New folder\desds.txt
#below notification is forr deletion of the file and directory, resetting the registry with new file changes.
2017-04-24 02:14:02.748  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : Detected file create/change event at: C:\temp\New folder (3)\hey.txt
2017-04-24 02:14:02.853  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : registering C:\temp\New folder (3) in watcher service
2017-04-24 02:14:17.289  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : registering C:\temp\New folder (4) in watcher service
2017-04-24 02:14:35.965  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : Detected file create/change event at: C:\temp\New folder (4)\New Text Document.txt
2017-04-24 02:14:35.966  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : Detected file create/change event at: C:\temp\New folder (4)\New Text Document.txt
2017-04-24 02:14:35.970  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : registering C:\temp\New folder (4) in watcher service
## below notification is for creation of the file, resetting the registry with new file changes.
2017-04-24 02:14:39.498  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : Detected file create/change event at: C:\temp\New folder (4)\New Text Document.txt
2017-04-24 02:14:39.498  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : Detected file create/change event at: C:\temp\New folder (4)\dfrg.txt
2017-04-24 02:14:39.499  INFO 7016 --- [pool-1-thread-1] c.e.s.cache.RecursiveWatcherService      : registering C:\temp\New folder (4) in watcher service
