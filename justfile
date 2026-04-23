# Copyright (c) 2026 Elektrobit Automotive GmbH
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0

bitbake-setup-init:
	./scripts/bitbake-setup-init.sh

build-sysvinit-minimal-qemu: bitbake-setup-init
	bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-sysvinit machine/qemux86-64
	bash -c '. bitbake-builds/ankaios-sysvinit-qemux86-64/build/init-build-env && bitbake core-image-minimal'

run-sysvinit-minimal-qemu: build-sysvinit-minimal-qemu
	bash -c '. bitbake-builds/ankaios-sysvinit-qemux86-64/build/init-build-env && runqemu snapshot nographic slirp'

build-sysvinit-minimal-rpi4: bitbake-setup-init
	bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-sysvinit machine/raspberrypi4-64
	bash -c '. bitbake-builds/ankaios-sysvinit-raspberrypi4-64/build/init-build-env && bitbake core-image-minimal'

build-sysvinit-full-rpi4: bitbake-setup-init
	bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-sysvinit machine/raspberrypi4-64
	bash -c '. bitbake-builds/ankaios-sysvinit-raspberrypi4-64/build/init-build-env && bitbake core-image-full-cmdline'

build-sysvinit-minimal-rpi5: bitbake-setup-init
	bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-sysvinit machine/raspberrypi5
	bash -c '. bitbake-builds/ankaios-sysvinit-raspberrypi5/build/init-build-env && bitbake core-image-minimal'

build-systemd-full-qemu: bitbake-setup-init
	bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-systemd machine/qemux86-64
	bash -c '. bitbake-builds/ankaios-systemd-qemux86-64/build/init-build-env && bitbake core-image-full-cmdline'

run-systemd-full-qemu: build-systemd-full-qemu
	bash -c '. bitbake-builds/ankaios-systemd-qemux86-64/build/init-build-env && runqemu snapshot nographic slirp'

build-systemd-full-rpi4: bitbake-setup-init
	bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-systemd machine/raspberrypi4-64
	bash -c '. bitbake-builds/ankaios-systemd-raspberrypi4-64/build/init-build-env && bitbake core-image-full-cmdline'

build-systemd-full-rpi5: bitbake-setup-init
	bitbake-setup init --non-interactive ./bitbake-setup-ankaios.conf.json ankaios-systemd machine/raspberrypi5
	bash -c '. bitbake-builds/ankaios-systemd-raspberrypi5/build/init-build-env && bitbake core-image-full-cmdline'

lint:
	@set -eu; \
	files="$(find recipes-ankaios -type f -name '*.bb' | sort)"; \
	failed=0; \
	for file in $files; do \
		echo "PARSED: $file"; \
		output="$(oelint-adv --quiet --hide info --relpaths "$file" 2>&1 || true)"; \
		if [ -n "$output" ]; then \
			printf '%s\n' "$output"; \
			failed=1; \
		fi; \
	done; \
	[ "$failed" -eq 0 ]
