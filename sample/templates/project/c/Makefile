all:

.PHONY: target
target:
	mkdir -p $@

.PHONY: build
build: target/main

.PHONY: clean
clean:
	rm -rf target

target/%.o: src/%.c target
	gcc -c $< -o $@

target/main: target/main.o
	gcc $^ -o $@

