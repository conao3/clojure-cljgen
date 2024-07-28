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

# Config dir
Config dir is below structure.

- `template` folder is required.
- You can put any directory in between. (and this structure into template name)
- `.cljgen.yml` is marker file to indicate template base-dir.
```
<config-dir>
└── template
    └── <path/to/any/template>
       └── <template-name>
          ├── .cljgen.yml
          ├── ...
          └── ...
```

# Sample
## sample/templates/file/readme
Simple one-file example.
```
sample/templates/file/readme/
├── .cljgen.yml
└── README.md
```

## sample/template/project/c
Simple project.
```
sample/templates/project/c/
├── .cljgen.yml
├── Makefile
└── src
    └── main.c
```

## sample/template/project/clojure
Simple project with parameterize path.
```
sample/templates/project/clojure/
├── .cljgen.yml
├── deps.edn
├── Makefile
├── src
│   └── {{repo-name}}
│       └── main.clj
└── test
    └── {{repo-name}}
        └── main_test.clj
```
