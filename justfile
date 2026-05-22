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

RELEASE := "wrynose"
BITBAKE_SETUP_CONFIG := "./bitbake-setup-ankaios-" + RELEASE + ".conf.json"

bitbake-setup-repo:
	./scripts/bitbake-setup-repo.sh

# Run init or update depending on whether the setup directory already exists
[private]
bitbake-setup-init-or-update runtime machine:
	#!/usr/bin/env bash
	set -euo pipefail
	setup_dir="bitbake-builds/ankaios-{{runtime}}-{{RELEASE}}-{{machine}}"
	if [[ -d "${setup_dir}/layers" ]]; then
		bitbake-setup update --setup-dir "${setup_dir}"
	else
		bitbake-setup init --non-interactive {{BITBAKE_SETUP_CONFIG}} ankaios-{{runtime}} machine/{{machine}}
	fi

# Build wrynose minimal sysvinit image for QEMU (override with RELEASE=whinlatter)
build-sysvinit-minimal-qemu: bitbake-setup-repo (bitbake-setup-init-or-update "sysvinit" "qemux86-64")
	bash -c '. bitbake-builds/ankaios-sysvinit-{{RELEASE}}-qemux86-64/build/init-build-env && bitbake core-image-minimal'

# Build and run wrynose minimal sysvinit image in QEMU (override with RELEASE=whinlatter)
run-sysvinit-minimal-qemu: build-sysvinit-minimal-qemu
	bash -c '. bitbake-builds/ankaios-sysvinit-{{RELEASE}}-qemux86-64/build/init-build-env && runqemu snapshot nographic slirp'

# Build wrynose full-cmdline systemd image for QEMU (override with RELEASE=whinlatter)
build-systemd-full-qemu: bitbake-setup-repo (bitbake-setup-init-or-update "systemd" "qemux86-64")
	bash -c '. bitbake-builds/ankaios-systemd-{{RELEASE}}-qemux86-64/build/init-build-env && bitbake core-image-full-cmdline'

# Build and run wrynose full-cmdline systemd image in QEMU (override with RELEASE=whinlatter)
run-systemd-full-qemu: build-systemd-full-qemu
	bash -c '. bitbake-builds/ankaios-systemd-{{RELEASE}}-qemux86-64/build/init-build-env && runqemu snapshot nographic slirp'

# Build wrynose minimal sysvinit image for Raspberry Pi 4 (override with RELEASE=whinlatter)
build-sysvinit-minimal-rpi4: bitbake-setup-repo (bitbake-setup-init-or-update "sysvinit" "raspberrypi4-64")
	bash -c '. bitbake-builds/ankaios-sysvinit-{{RELEASE}}-raspberrypi4-64/build/init-build-env && bitbake core-image-minimal'

# Build wrynose full-cmdline sysvinit image for Raspberry Pi 4 (override with RELEASE=whinlatter)
build-sysvinit-full-rpi4: bitbake-setup-repo (bitbake-setup-init-or-update "sysvinit" "raspberrypi4-64")
	bash -c '. bitbake-builds/ankaios-sysvinit-{{RELEASE}}-raspberrypi4-64/build/init-build-env && bitbake core-image-full-cmdline'

# Build wrynose full-cmdline systemd image for Raspberry Pi 4 (override with RELEASE=whinlatter)
build-systemd-full-rpi4: bitbake-setup-repo (bitbake-setup-init-or-update "systemd" "raspberrypi4-64")
	bash -c '. bitbake-builds/ankaios-systemd-{{RELEASE}}-raspberrypi4-64/build/init-build-env && bitbake core-image-full-cmdline'

# Build wrynose minimal sysvinit image for Raspberry Pi 5 (override with RELEASE=whinlatter)
build-sysvinit-minimal-rpi5: bitbake-setup-repo (bitbake-setup-init-or-update "sysvinit" "raspberrypi5")
	bash -c '. bitbake-builds/ankaios-sysvinit-{{RELEASE}}-raspberrypi5/build/init-build-env && bitbake core-image-minimal'

# Build wrynose full-cmdline systemd image for Raspberry Pi 5 (override with RELEASE=whinlatter)
build-systemd-full-rpi5: bitbake-setup-repo (bitbake-setup-init-or-update "systemd" "raspberrypi5")
	bash -c '. bitbake-builds/ankaios-systemd-{{RELEASE}}-raspberrypi5/build/init-build-env && bitbake core-image-full-cmdline'

lint:
	@set -eu; \
	files="$(find recipes-ankaios recipes-helpers recipes-kernel -type f -name '*.bb' | sort)"; \
	failed=0; \
	for file in $files; do \
		echo "PARSED: $file"; \
		output="$(oelint-adv --quiet --hide info --relpaths "$file" 2>&1 | grep -v '\.inc:' || true)"; \
		if [ -n "$output" ]; then \
			printf '%s\n' "$output"; \
			failed=1; \
		fi; \
	done; \
	[ "$failed" -eq 0 ]
