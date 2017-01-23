JSCover2
========

A JavaScript code-coverage tool

[![Build Status](https://travis-ci.org/tntim96/JSCover2.svg?branch=master)](https://travis-ci.org/tntim96/JSCover2)
[![Coverage Status](https://coveralls.io/repos/tntim96/JSCover2/badge.png?branch=master)](https://coveralls.io/r/tntim96/JSCover2?branch=master)
[![Dependency Status](https://www.versioneye.com/user/projects/5404420cd0734e31f400016d/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5404420cd0734e31f400016d)

Motivations
* Swap from Rhino to the excellent [Closure-Compiler](https://developers.google.com/closure/compiler/) for several reasons:
  * ES6 Harmony support
  * Designed specifically for AST manipulation and JavaScript code generation
  * Superior code position data for reports
* Modify the instrumentation to better support non-browser environments like Nashorn (NB: test suite uses Nashorn)
* Make build more modular for different environments: API, CLI, browser