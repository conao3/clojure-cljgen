# clojure-cljgen

# Usage
## Subcommand
### `list`
List template names.

### `gen`
Generate template.

- `--template <template>`: Specify template name to generate.
- `-C, --change-dir <dir>` (default: `current-directory`): Specify directory to generate basedir.

## Global options
### `-h, --help`
Show help.

### `--config-dir <dir>` (default: `~/.config/cljgen`)
Specify config dir.

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
├── src
│   └── {{repo-name}}
│       └── main.clj
└── test
    └── {{repo-name}}
        └── main_test.clj
```
