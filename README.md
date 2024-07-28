# clojure-cljgen

# Sample
## sample/templates/file/readme
Simple one-file example.
```
sample/templates/file/readme/
└── README.md
```

## sample/template/project/c
Simple project.
```
sample/templates/project/c/
├── Makefile
└── src
    └── main.c
```

## sample/template/project/clojure
Simple project with parameterize path.
```
sample/templates/project/clojure/
├── deps.edn
├── Makefile
└── src
    └── {{repo-name}}
        └── main.clj
```
