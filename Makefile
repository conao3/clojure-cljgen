all:

.PHONY: build
	nix build

.PHONY: deps-lock
deps-lock: deps-lock.json

deps-lock.json: deps.edn
	nix run github:jlesquembre/clj-nix#deps-lock -- --deps-include $<
