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
