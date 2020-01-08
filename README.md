# active-viz

A ClojureScript library containing helpers for data visualization

[![Actions Status](https://github.com/active-group/active-viz/workflows/Tests/badge.svg)](https://github.com/active-group/active-viz/actions)


[![Clojars Project](https://img.shields.io/clojars/v/de.active-group/active-viz.svg)](https://clojars.org/de.active-group/active-viz)

## Overview

This library provides tooling for data visualizations. At this point it is restricted to charts:

* It provides Scales, an entity to map domains to ranges. This is useful to map data to viewports. Scales are composable.
* It provides ticks calculation. This is useful for defining nice ticks on axis based on several demands.
* It provides various interpolations for smooth curves. 
* It provides path utilities to generate path strings for svg rendering.

## Development

Test with 

    lein fig:test

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
