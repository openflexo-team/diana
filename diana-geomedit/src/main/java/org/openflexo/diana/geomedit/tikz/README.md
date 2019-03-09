# The Tikz Module
*Please read the README located under diana\diana-tikz\src\main\java before you read this one*

This module defines a set of tools designed to interface the **Tikz** language with the **GeomEdit** constructions. It is based on the **Diana-Tikz** project which provides an intermediate representation with which it is easier to interface than with some code.


## Global architecture

The **Tikz** module is mainly build with classes which are extensions of the classes from the **Diana-Tikz** project. These extended classes are now specific to the **GeomEdit** representation.

### Building over the **Diana-Tikz** project

First, the *TikzLinkExtended* class is an extension of the *TikzLink* class and provides all the linking tools that are specific to the **GeomEdit** representation. The main tool is the method that produces a key from a *GeometricConstruction* object.

The *TikzFactory* class has also been extended to the *TikzFactoryExtended* class, that provides all the functions needed to build or update a *TikzConstruction* or a *GeometricConstruction* object from its associated construction object. It also includes some *ValueConverter* objects that optimize the conversion between two formats of a same enumeration, such as colors or shapes.

Finally, the *TikzConnector* class has been extended to the *TikzConnectorExtended* class, that provides all the functions needed to switch from one representation to the other. This class has the controller role in the **Tikz** module.
 
### Integration in the existing system

Even if the provided functionalities were designed to be as little intrusive as possible, the module has to be integrated in the main system. Therefore, an attribute from the *TikzConnectorExtended* class has been added as an attribute of the *GeomEditEditor* class. As this class already holds the drawing controller (*GeomEditDrawingController*) and the drawing model (*GeometricDiagram*), the connector can easily reach them.


## Switching from one representation to the other

The main difference between this representations switching and the one performed in the **Diana-Tikz** project is that this one has to be as little destructive as possible. Whereas the intermediate representation is rebuild from scratch when the **Tikz** code is analyzed, is it a better practice here to modify and update the constructions.

### Updating an intermediate representation from a **GeomEdit** construction

As described in the README of the **Diana-Tikz** project, the *TikzConstruction* objects provide two ways to access their *ParameterValue* attributes. The way used here is the one based on specific accessors, as the *TikzConstruction* objects are structured and not made of a list containing all parameters.

Therefore, an intermediate representation is updated parameter after parameter, using its setters and the getters of the *GeometricConstruction* objects. A particular attention is payed to the localization of the construction (when it is a node construction) because there are several ways to set it, that depend on the *GeometricConstruction* type.

When a new *TikzConstruction* has to be build from a newly added construction, an empty *TikzConstruction* is provided and then updated, using the existing functions.

### Updating a **GeomEdit** construction with an intermediate representation

The same way is used to update the existing *GeometricConstruction* objects or create new ones, as they have setters and the *GeometricConstructionFactory* object used does provide the needed creation functions.

As said in the previous section, both representations have several ways to indicate their position (relative or absolute, using Cartesian or polar coordinates, etc) and the type of an existing node is not always appropriate to the format of its new position. It is the only case where a *GeometricConstruction* object has to be deleted and rebuild from scratch. All the others parameters can be modified using setters.

### End-to-end updates

In order to update **Tikz** code from **GeomEdit** constructions or the opposite, the switching functions described here and in the other README are combined. The main scheme consists of the following steps :
* Creating an intermediate representation from the **Tikz** code
* Creating the *TikzLinkExtended* objects from the representation that will be updated (the intermediate one or the **GeomEdit** one)
* Updating the links using the other representation
* Updating the first representation using the second one
* If the intermediate representation has been updated, rewrite the **Tikz** code

The sequence of the links updates permit to take in account the needed creations and deletions of construction objects in the first representation.


## User interface

In order to be used, the **Tikz** module has to be reachable from the user interface. That is why a *TikzEditorFrame* object has been created, providing both a text editor in which the **Tikz** code can be edited and two buttons that trigger the end-to-end updates.

An option has been added in the *GeneralContextualMenu*, which opens the window described by the *TikzEditorFrame*.


## Current scope of application and needed updates

As this package is based on the **Diana-Tikz** project, a part of its current scope of application and needed updates are yet described in the README of the project.

Here is the scope of application specific to **GeomEdit** :
* A code editor with two tools to push the code to the constructions or pull from it
* Nodes build from center and size or from two point are supported
* Nodes build from an other node position with a null distance from it are supported
* Connectors in their actual state (only build between to node centers) are fully supported
* The standard colors for the edges and name of the nodes as well as some shapes (rectangle and circle) are supported

Most of the possible updates are described in the README of the **Diana-Tikz** project, but these can be added to the list :
* Add a grammar checker in the **Tikz** editor
* Add an option to directly compile the produced **Tikz** code to a pdf file
* Support the presence of *GeometricConstruction* objects that should not be taken in account when producing some **Tikz** code