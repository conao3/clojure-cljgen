all:

TARGET := cljgen

.PHONY: clean
clean:
	rm -rf target

.PHONY: build
build: clean
	clojure -T:build uberjar

.PHONY: build.native
build.native: target/$(TARGET)

target/$(TARGET): build
	native-image \
		-jar target/$(TARGET)-standalone.jar \
		-H:+ReportExceptionStackTraces \
		--report-unsupported-elements-at-runtime \
		--features=clj_easy.graal_build_time.InitClojureClasses \
		--verbose \
		--no-fallback \
		$@

.PHONY: deps-lock
deps-lock: deps-lock.json

deps-lock.json: deps.edn
	nix run github:jlesquembre/clj-nix#deps-lock -- --deps-include $<
