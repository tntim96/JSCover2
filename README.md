JSCover2

A JavaScript code-coverage tool

[![Build Status](https://drone.io/github.com/tntim96/JSCover2/status.png)](https://drone.io/github.com/tntim96/JSCover2/latest)

Motivations
* Swap from Rhino to the excellent Closure-Compiler for several reasons:
** ES6 Harmony support
** Designed specifically for AST manipulation and JavaScript code generation
** Superior code position data for reports
* Modify the instrumentation to better support non-browser environments like Nashorn (NB: test suite uses Nashorn)
* Make build more modular for different environments: API, CLI, browser