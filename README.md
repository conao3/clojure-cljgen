# cljgen

A flexible template generator for Clojure projects and files.

## Overview

cljgen is a command-line tool that generates project scaffolding and files from customizable templates. It supports parameterized templates with EDN-style configuration, making it easy to create consistent project structures.

## Installation

Clone this repository and build with your preferred Clojure build tool.

## Usage

### Commands

#### `list`

List all available template names:

```bash
cljgen list
```

#### `gen`

Generate files from a template:

```bash
cljgen gen --template <template-name> [options] [params]
```

**Options:**

| Option | Default | Description |
|--------|---------|-------------|
| `--template <template>` | (required) | Template name to generate |
| `-C, --change-dir <dir>` | Current directory | Base directory for generated output |

**Parameters:**

Pass template parameters using EDN syntax:

```bash
cljgen gen --template project/clojure '{:repo-name "cljgen"}'
```

### Global Options

| Option | Default | Description |
|--------|---------|-------------|
| `-h, --help` | - | Show help information |
| `--config-dir <dir>` | `~/.config/cljgen` | Configuration directory path |

## Configuration

### Directory Structure

The configuration directory follows this structure:

```
<config-dir>/
└── template/
    └── <path/to/template>/
        └── <template-name>/
            ├── .cljgen.yml
            └── ... (template files)
```

**Key points:**

- The `template` folder is required
- Nested directories are supported and become part of the template name
- `.cljgen.yml` marks the root of each template

## Examples

### Simple File Template

A single-file README template:

```
sample/templates/file/readme/
├── .cljgen.yml
└── README.md
```

### Basic Project Template

A C project with standard structure:

```
sample/templates/project/c/
├── .cljgen.yml
├── Makefile
└── src/
    └── main.c
```

### Parameterized Project Template

A Clojure project with dynamic path substitution using `{{repo-name}}`:

```
sample/templates/project/clojure/
├── .cljgen.yml
├── deps.edn
├── Makefile
├── src/
│   └── {{repo-name}}/
│       └── core.clj
└── test/
    └── {{repo-name}}/
        └── core_test.clj
```

## License

See LICENSE file for details.
