## About
![Android](https://img.shields.io/badge/android-broken-red?style=for-the-badge) ![linux](https://img.shields.io/badge/linux-passing-green?style=for-the-badge) ![win32](https://img.shields.io/badge/windows-passing-green?style=for-the-badge)
<br>
JEPL is interpreting JVM language, that allows to create projects like build scripts, games, applications, etc. <br>
<a href="https://github.com/Monsler/JEPL/blob/main/docs/docs-en.md">Documentation 🇬🇧</a>
<br>

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
