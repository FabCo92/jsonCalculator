# Java Challenge

## Problem to be solved

Please create a variable data evaluation that is to be supplied with a data file and an instruction file and generate an output file. All files are in a json format. 
The structure of the files is not given by a schema and is only documented by the example files.

The input data is in a simple format: Each file contains a root object with one member `entries`. It defines a list of data points and can contain a variable number of members with child objects.
In our example, each data point is a city and has three members. One member is always present, this is called `name` and is used for filtering.

The operations are similar in structure. It contains an object with the member operations which defines a list of operation objects.
An operation always has five members:
- `name` - The name to be used for the output.
- `function` - The function to be evaluated, this can be `min`, `max`, `sum` or `average`.
- `field` – An array of member names to access the value for the operation.
- `filter` - A regular expression to be applied to the `name` member. Only those "entries" that match the regular expression should be included in the evaluation.

The output also consists of a list of objects which contain the operation name and the formatted calculated value. 
Floating point numbers are to be output with exactly two decimal places.

# Solution

## Description

The solution works with various data sets and schemas for legit arguments.
There can be as many nested attributes as possible in the data.json file.
For filters with no matching data object every function will return 0.00.
Wrong json files and arguments will cause an explanatory exception.

## Complexity

Complexity of the algorithm to handle already parsed Maps of Data Input (N) and Operations (M) should be in O(m*n) (For every operation in M every data object in N will be evaluated). 

## Tools
| Tool    | Category              | Explanation                                          |
|---------|-----------------------|------------------------------------------------------|
| `Maven` | build tool            | personal experience                                  |
| `JUnit` | test framework        | personal experience                                  |
| `json-simple` | Java Toolkit for JSON | Easy to use and very flexible for unknown data input |
