# codegen-mojo

A maven plugin for data-driven, template-based text file generation.

## Features

The codegen-mojo performs template expansion during the Maven generate-sources
phase.  Its inputs are YAML files placed into the source tree, under
`src/main/codegen` by default.  Each YAML file specifies one or more template
expansions to be performed.  The YAML file syntax is specified below.  Templates
that require pre-building must 

Currently, the codegen-mojo handles only [Jamon](http://www.jamon.org) templates,
though support for other tools is in the works.  Jamon was chosen first because it
is full-featured, generates strongly typed Java code, and comes with a Maven
plug-in.

While the default usage of this maven plugin is to generate Java source code, it
may be used to generate any type of text file.

Planned for 1.0:

- Finish implementation of Java metamodel references.
- Support for some other templating tool (velocity?)
- Document plugin XML, YML spec format, usage patterns.

## Project Structure

plugin
- the plugin project itself

samples
- sample template applications

## Plugin Configuration

TBD

## YAML Schema

TBD
