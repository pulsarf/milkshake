.PHONY: watch
watch:
	@if [ -n "$$OS" ] && [ "$$OS" = "Windows_NT" ] && [ ! -d /proc/sys/fs/binfmt_misc/WSL ]; then \
		wsl make; \
	else \
                rm ./mod/mod.jar -f; \
                rm /mnt/d/olympiad/EthyrMC-master/clients/fabric-loader-0.16.14-1.21/mods/mod.jar -f; \
		bazel build @milkshake//mod --output_groups=default,executable; \
		cp bazel-bin/mod/mod.jar ./mod; \
                cp bazel-bin/mod/mod.jar /mnt/d/olympiad/EthyrMC-master/clients/fabric-loader-0.16.14-1.21/mods; \
	fi