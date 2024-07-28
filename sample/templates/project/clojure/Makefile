all:

TARGET := {{ repo-name }}

.PHONY: test
test:
	clj -M:test

.PHONY: clean
clean:
	rm -rf target

.PHONY: build
build: clean
	clean -T:build uverjar

.PHONY: build.native
build.native: target/${TARGET}

target/$(TARGET): build
	native-image \
		-jar target/$(TARGET)-standalone.jar \
		-H:+ReportExceptionStackTraces \
		--report-unsupported-elements-at-runtime \
		--features=clj_easy.graal_build_time.InitClojureClasses \
		--verbose \
		--no-fallback \
		$@
