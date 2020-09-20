# Java Directory Server 
Cross platform build of directory server written in Java

[![Scrutinizer Code Quality](https://scrutinizer-ci.com/g/starpeace-project/java-directory-server/badges/quality-score.png?b=master)](https://scrutinizer-ci.com/g/starpeace-project/java-directory-server/?branch=master)

The first step to modernising the existing code base begins with the Starpeace universes main server, the Directory Server.

This is the server that controls logins, server configuration, subscriptions and many more.
By modernising here first we can remove our reliance on Microsoft Servers and Databases, moving to the more economical Mysql.

This will be a hybrid directory server as it will create the bridge between the legacy Microsoft components we use to the more modern cross platform alternatives we need to move towards to lower running costs and improve performance and stability.

At a later (much) date, when there is no longer a need for data migration, those components will be removed and the server streamlined.

## Why Java?

So we are using Java here for a couple of reasons, Java is both easy to work with and more than powerful/fast enough to handle the demands we will be placing on it, it is as truly cross platform as we need for the server infrastructure, we can develop with it at good speed and produce results in a timely fashion. Java also has the JavaFX module which makes our life with GUI building far easier than most other languages, not only can we mock and build GUI components fast but with JavaFX we can "skin" them as well using css, which while not greatly important for the servers, should we decide to tackle the launcher/updater/client with it will be extremely handy.

## Data migration

This server will always attempt to work with Mysql first, so for example on a login attempt, we will look for the login in mysql first and if not present, perform the login request against the Mssql database, if an account is found, we spawn a new thread to transfer the Mssql account accross to the Mysql server whilst returning our reply to the client performing the login request.

Using the above method on all calls, we will be be able to progressively move only relevant data from one system to the other. For example Starpeace Online has in excess of 370,000 user accounts, a fraction of which are used, and the way storage works currently make cleaning them out a dangerous thing, so progressive migration a much more sensible solution.


The table below will grow as features are thought of and added, regards the circles, :red_circle: denotes less than 50% done, :yellow_circle: 50% done or more and :green_circle: denotes 100% done, no intention to add more to that area, only optimisation and fixes there after.


| Component | Feature | Description | Status | Progress | Complete |
|-----------|:-------:|:-----------:|:------:|:--------:|:--------:|
| Application | Self Configuring | Ability to pre configure so the package is all thats needed to get up and running | Complete | 100% | :green_circle: |
| Read Only Server | .. | Threadpool based multithreaded server | Working | 30% | :red_circle: |
| Write Server | .. | Threadpool based multithreaded server | Working | 30% | :red_circle: |
| Mysql | Connector | Connection pool based | Not working | 5% | :red_circle: |
| Mssql | Connector | Connection pool based | Not working | 5% | :red_circle: |
| Read Only Server | Ip Black List | Prevent access | Working | 100% | :green_circle: |
| Write Server | Ip White List | Restrict access | Working | 100% | :green_circle: |
| Read Only Server | Login Table | Show logins | Working | 100% | :green_circle: |
| Read/Write Servers | RDO Library | Enable legacy communication | Not Working | 0% | :red_circle: |
| Read/Write Server | Command Processor | Handle incoming commands | Working | 1% | :red_circle: |
| All | Event bus | Enable non blocking gui updates | Working | 90% | :yellow_circle: |


## Contributions

As with all open source projects contibutions are greatly welcomed, should you wish to contribute to the project, then please raise an issue above and we will contact you (please don't leave email addresses etc, we don't need them to find you), this is not a members only club, it just allows us to direct you to areas not being worked on or where help may be needed.
The PR Template/Breakdown can be found [Here](PR_TEMPLATE.md)