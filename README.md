Installation
============

The VRaptor plugin is listed in the Forge plugin repository so installation is trivial. 
In Forge type: 
  
    forge install-plugin vraptor

That's it! The plugin will be downloaded and installed.

Creating a new project
======================

    new-project --named myproject --topLevelPackage com.mylabs.myproject

Setting up the pesistence
=========================

The vraptor beans are not managed by container, but by yourself. Therefore, we'll create a non jta datasource to connect to JBoss AS 7 Example DataSource.

    persistence setup --named default --provider HIBERNATE --container CUSTOM_NON_JTA --jndiDataSource java:jboss/datasources/ExampleDS

Setting up the vraptor scaffold
===============================

The following command will create the layout files, index jsp and index controller.

    scaffold setup --scaffoldType vraptor

Creating a JPA Entity
=====================

Now, we will create an simple entity and some fields.

    entity --named Person
    field string --named name
    field int --named age

Creating the user interface for an entity
=========================================

    scaffold from-entity
    
Building the project
====================

    build

Deploying on JBoss AS 7
=======================

    forge install-plugin jboss-as-7
    as7 setup
    as7 deploy (with the server on)

Contribute
==========

This plugin is open source and is waiting for your contributions. Please fork this plugin and hack away!
