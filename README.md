## Synopsis

Simple java stream utils that allows throwing and catching of exceptions in functions

## Code Example

Basically wraps the usual try-catch in a 'Try' object. A 'Try' object contains the value of executed function or the caught exception.
'TryCollectors' can be used to manage streams of 'Try' objects and collect results or checks for successfully run functions.

## Motivation

I got really annoyed with the ugly code required to handle exceptions with the java8 stream api. So I decided to write a library to help write nicer code that uses streams and throws exceptions.

## License

MIT license