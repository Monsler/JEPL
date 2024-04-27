## ${\color{blue}JEPL \space Documentation}$
# Printing a text
To print a text on the single line, use <b>print</b>.<br>
```json
[
  {
    "name": "print",
    "args": ["\"Hello, world!\""]
  }
]
```
<code style="color : red">NOTE: If you put more arguments into this array, it will print your text, for example, if you put [1, 2, 3], it will print you 1 2 3.</code><br>
However, if you want to print the text on multiple lines, use this:
```json
[
  {
    "name": "print",
    "args": ["\"Hello, world!\"", "\"Some text.\""]
  }
]
```
It will print something like:<br>
```
Hello, world!
Some text.
```

# Getting input
Syntax: <br>

```json
{
  "name": "input",
  "args": [text, variable]
}
```
Getting user name example:<br>
```json
[
  {
    "name": "input",
    "args": ["\"Enter your name:\"", "\"name\""]
  },
  {
    "name": "println",
    "args": ["\"Hello, \"+name"]
  }

]
```
```
Enter your name:
Monsler
Hello, Monsler
```

# Importing a library 
You can add more features to language by importing built-in libraries into your script.<br>
Syntax:<br>
```json
{
  "name": "import",
  "args": [lib_name, ...]
}
```

```
...
```
 is mean that you can import more than one library inside the block.<br>
Example:<br>
```json
[
  {
    "name": "import",
    "args": ["\"io\""]
  },
  {
    "name": "io_put_contents",
    "args": ["\"file.md\"", "\"Hello from JEPL \"+jeplversion"]
  }
]
```

# Function and it's arguments
Syntax:
```json
{
   "name": "function",
   "args": ["\"main\""],
   "body": [...]
}
```
Example:<br>
```json
[
{
  "name": "function",
  "args": ["\"printWithDots\""],
  "body": [
    {
      "name": "print",
      "args": ["\".\"+arg1+\".\""]
    }
  ]
},
{
  "name": "jump",
  "args": ["\"printWithDots\"", "\"JEPL!\""]
}
]
```
Output: <br>
```
.JEPL!.
```

# Including other scripts into one
Syntax: <br>
```json
{
  "name": "include",
  "args": [filename, ...]
}
```
Example:<br>

main.jepl:
```json
[
  {
    "name": "include",
    "args": ["\"fun.jepl\""]
  },
  {
    "name": "jump",
    "args": ["\"Fun\"", "\"Hello!\""]
  }
]
```
fun.jepl:
```json
[
  {
    "name": "function",
    "args": ["\"Fun\""],
    "body": [
      {
        "name": "println",
        "args": ["\"<\"+arg1+\">\""]
      }
    ]
  }
]
```
Output:
```
<Hello!>
```