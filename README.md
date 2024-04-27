## About
JEPL is interpreting JVM language, that allows to create projects like build scripts, games, applications, etc. <br>
<a href="https://github.com/Monsler/JEPL/docs/docs-en.md">Documentation 🇬🇧</a>
<a href="https://github.com/Monsler/JEPL/docs/docs-ru.md">Documentation 🇷🇺</a>

## Hello world
```json
[
  {
    "name": "function",
    "args": ["\"main\""],
    "body": [
      {
        "name": "println",
        "args": ["\"Hello, World!\""]
      }
    ]
  }
]
```

## Variables
```json
[
  {
    "name": "variable",
    "args": ["\"name\"", "\"John\""]
  },
  {
    "name": "println",
    "args": ["\"Hello, \" + name"]
  }
]
```
