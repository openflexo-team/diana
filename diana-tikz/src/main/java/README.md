# The Diana-Tikz Project

This project defines a set of tools designed to interface the **Tikz** language with any other geometric representation. As these functionalities were aimed to be as little intrusive as possible, all classes defined here are totally independent from the interfaced representation.


## Global architecture

The main idea of this module is to build an intermediate representation between the **Tikz** language and the interfaced representation. This intermediate representation aims to keep all informations written in the **Tikz** code and to provide them in an object-oriented way.

### The intermediate representation structure

The intermediate representation is composed of a list of *TikzConstruction* objects. Each object can either be a *TikzConstructionNode* or a *TikzConstructionDraw* instance and represents a `\node` or a `\draw` construction respectively.

A *TikzConstruction* mainly contains a list of *ParameterValue* objects which are basically containers for any type. With these containers, every parameter value can be stored in this unique list, whether they are character strings, numerical values, 2-dimensional position (*ParameterValuePosition*) or numerical values with a unit (*ParameterValueValueWithUnit*).

As the *TikzConstruction* object is meant to be an intermediate representation, there are two ways to access its parameter values. The first one is based on keywords so it can easily be linked with a written representation, such as code, whereas the second one is based on getter and setter methods so it can easily be linked with an other object-oriented representation.
* In order to access a parameter value from a keyword, the *ParameterValue* objects are actually stored in a hashtable whose keys are the different values of an enumeration, each one corresponding to a different keyword.
* The second way to access a parameter value is implemented in the *TikzConstructionNode* and the *TikzConstructionDraw* objects. These have a direct access to each *ParameterValue* object which are their attributes. Therefore accessors can easily be provided for each parameter value.

### Linking with the interfaced representation

As each *TikzConstruction* object is aimed to correspond to one object of the interfaced representation, the chosen linking architecture provides a one-one correspondence between both representation. It consists of linking objects that point to one construction object of each representation.

In order to reach one construction object from the corresponding one, the linking objects are stored in a hashtable whose keys are computed from the construction objects themselves. As a consequence, the keys computed from two corresponding construction objects must be the same.

The abstract class *TikzLink* defines a half of these linking objects by providing all the tools for the intermediate representation side. A full class might be build as an extension of this one.


## Switching from one representation to the other

The *TikzConnector* class provides all the tools needed to switch between the intermediate representation and the **Tikz** code. A full class might be build as an extension of this one to provide the tools needed to switch between the intermediate representation and the interfaced one.

### Building an intermediate representation from some **Tikz** code

There are two steps to build an intermediate representation from some **Tikz** code. The first one is to parse this code and build an AST from it. The parser used was build from a *sablecc* code, so all its classes were automatically generated.

Then the second one consists of browsing this AST to build the different *TikzConstruction* objects. This AST contains a list of commands which are given to a *TikzFactory* object. This factory builds the intermediate construction object corresponding to the given command type and then sets its parameters, using the keywords to reach each *ParameterValue*.

### Writing some **Tikz** code from an intermediate representation

All the challenge to write some **Tikz** code from an intermediate representation is to keep the organization of the code previously written. That means not only keeping the order of the commands or the parameters, but also keeping the carriage return and the number of blanks between every word or symbol. With that aim, the *TikzWriter* object uses not only the list of *TiksConstruction* objects, but also the AST produced by the parsing of the previous code.

First, the commands that exist in the previous code will be rewritten, then the added constructions will be translated in **Tikz** code.


## Current scope of application and needed updates

The current project does not work on the whole possibilities of the **Tikz** language. It lacks both grammar flexibility and parameters diversity.

Here are the supported **Tikz** commands :
* A `\node` command with a fixed writing layout (name, list of parameters and position)
    * Following parameters are available : `color`, `draw` and `shape`
    * The position can either be an other node or an absolute position
* A `\draw` command with a start node and an end node.

Here are all the updates that could be provided to expand the possibilities of the **Diana-Tikz** project :
* For the `\node` command :
    * Expand the current parameters possibilities with more color formats and the shape `coordinate`
    * Support other parameters such as `fill`
    * Accept the parameters whose names are composed of several words
    * Support the relative position system with `node distance`, `below = of`, `below of = `, etc
    * Make the writing layout more flexible with the possibility to switch or omit some elements
    * Make the parameters writing more flexible with the possibility to omit either the name (when it is non-ambiguous) or the value (when the default value is expected)
    * Make it possible to give the same name to several nodes
* For the `\draw` command :
    * Support the absolute position and the node's particular points (north, east, etc) as start and end positions
    * Support parameters
* For the *TikzWriter* :
    * Keep the added carriage returns and blanks between each element as written by the user
    * When increasing the flexibility of the parser, keep the fidelity of the writer considering the order and presence of the elements 
