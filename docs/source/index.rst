:title: Platform Documentation
:description: An overview of the Platform Documentation
:keywords: platform, concepts, explanation

Welcome to the Firejack Platform's documentation!
=================================================

Be sure to see our 'how to' guides and 'tutorials' in the 'knowledge center' - accessable from the top menu of the firejack.org website.

The Firejack Platform allows developers, business people, designers, or anyone with a great idea to build and manage complex web services, applications, and integration solutions like mash-ups and portals. The Platform provides a ready-to-use user interface and services for commonly needed functions like user management, authorization control, content management, and configuration and enables rapid custom application development. Applications built using Firejack provide run-time management of application configuration, security, operations, and content once deployed.
The Platform encourages people to declare known asset (servers, databases, data objects, relationships, and supported actions to name a few) and document them in the Registry. Once done, Firejack can help anyone find, understand, document and share documentation, and even generate working applications and deploy them without writing a single line of code.
Firejack also marries a host of free and open source technologies into an integrated turnkey solution. These tools include Tomcat, Memcached, MySQL, Java, Maven, Spring, and Hibernate. The Platform uses declared assets to generate source code and compile them into deployment-ready web applications with complete supporting database structures. The source code requires only a handful of key integration points so that the Firejack Console administrative tools will continue to work properly. This allows developers to easily understand and modify data entry applications and use them as examples for more complex custom development as needed (usually centered around custom user interface development and not services).
Finally, Firejack allows for easy integration of multiple user directories and multiple outside data sources and service end points under a single umbrella. The most obvious benefit of this is single-sign-on, followed by the ability to quickly provide a common role and permission model across many systems (even if some are in the cloud or offered as SaaS solutions). Whether you build your entire infrastructure inside the Firejack Platform or use Firejack as a hub for bringing everything together, creating a uniform security and services solution that includes metrics and tracking, and a host of other benefits is always within reach.

Domain Driven - The Registry
The Firejack Registry (featured prominently on the Firejack Domain Management Screen) encourages administrators to add every logical system, physical server, database, file storage container, domain area, data object or entity, relationship, action, and process to a common hierarchy. Once entered into the Registry, Firejack assigns every asset a unique lookup name that applications may use to load meta-data for the asset like URL patterns, security information, connection information. This configuration meta-data let's applications refer to connection points, data sources, and even actions by logical name and allows code to move from one environment to another with ease. As long as logical assets are still present, their actual location can change without breaking the application.
The Registry also automatically generates detailed documentation and copies entered descriptions to the documentation engine with every edit. Every asset's description ends up on a documentation page along with it's lookup data and then becomes searchable through the Firejack Console Search mechanism. More complex registry entries like entities, actions, and relationships automatically list related objects, field and parameter descriptions, and even automatically generate working REST and SOAP invocation examples. Most content in the documentation pages is editable inline by an authorized administrator, including allowing for multi-language support and version control.
Last but not least, Firejack can leverage declared assets like packages, domains, entities, relationships, and actions to automatically generate complete, working services and database structures. Once generated, Firejack automatically provides a buildable, editable version of the source code and compiles the code into a ready-to-run web application that can be deployed right from the Firejack Console.
In combination, these three aspects of the Firejack Registry: declaration and lookup, documentation, and system generation, establish the foundation for everything else the Firejack Platform can do. Having one place to deal with your services, security, and system infrastructure is power even large organizations struggle with. The Firejack Registry puts that power at your fingertips.


.. toctree::
   :maxdepth: 5

   overview/index
   installation/index
   buildinggateway/index 	
   buildingconsole/index
   reverseengineering/index
   businessintelligencefeatures/index
   firejacksalesdocuments/index