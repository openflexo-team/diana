# DIANA framework

[/images/components/diana/DianaScreenshot] Diana

Diana is a pure-java drawing framework providing graphical editors above a Java model (a Java program).
  
It takes its originality by beeing able to associate a graph of interconnected objects with adapted graphical representations. 

Graphical representations are considered as graphical properties such as Color or Size, which are then associated to models depending on the context. 

Diana is based on Connie and Pamela frameworks. 

A Java Swing implementation of Diana has been developped for Openflexo.
  
Key features of DIANA

* Model-driven technology: fully respect the MVC pattern

* Non intrusive technology: there is no need to modify your initial code used as a model to be graphical represented and/or edited

* Provide Box/Arrow-like diagrams as well as complex geometrical constructions

* Rely on a vectorial graphics library

* Agnostic technology with multiple renderers (only Swing renderer is provided yet)

  
Contents of this package

DIANA project contains separate components (defined here as modules):

* [Diana-geom component](/diana-geom/index.md), is an extension of java2d library providing symbolic algebra for 2D geometry

* [Diana-api component](/diana-api/index.md), which is the API for Diana

* [Diana-core component](/diana-core/index.md), which contains core implementation for Diana (technology agnostic)

* [Diana-swing component](/diana-swing/index.md), which is a Swing rendering engine (Swing implementation for Diana-core)

Three other components are also provided, as application use cases of Diana (or experimentations):

* [Diana-DrawingEditor](/diana-drawing-editor/index.md), which is a stand-alone implementation of a Swing basic diagram editor

* [Diana-PowerPointEditor](/diana-ppt-editor/index.md), which is an experimentation for a stand-alone powerpoint editor (limited to one slide)
    
* [Diana-GeomEdit](/diana-geomedit/index.md), which is an experimental stand alone Swing application dedicated to 2D geometry buildings
  
