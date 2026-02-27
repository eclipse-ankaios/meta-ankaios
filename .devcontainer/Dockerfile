FROM ubuntu:24.04

ARG TARGETARCH
ARG USERNAME=vscode

# All the packages we need
RUN export DEBIAN_FRONTEND=noninteractive \
    && apt update \
    && apt -y install \
    # Basics
    git \
    zsh \
    curl \
    iputils-ping \
    sudo \
    build-essential \
    chrpath \
    cpio \
    debianutils \
    diffstat \
    file \
    gawk \
    gcc \
    libacl1 \
    liblz4-tool \
    locales \
    python3 \
    python3-git \
    python3-jinja2 \
    python3-pexpect \
    python3-pip \
    python3-subunit \
    python3-websockets \
    socat \
    texinfo \
    unzip \
    wget \
    xz-utils \
    zstd \
    kas \
    # Others
    tmux \
    vim \
    uidmap \
    fuse3 \
    fuse-overlayfs \
    slirp4netns

# User management
RUN (userdel -r ubuntu || true) \
    && groupadd rustlang \
    && useradd -s /bin/bash -d /home/${USERNAME} -m -G rustlang ${USERNAME} \
    && echo "${USERNAME} ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/${USERNAME}

# Prepare shells
USER ${USERNAME}
COPY --chown=${USERNAME}:${USERNAME} dot_bashrc /home/${USERNAME}/.bashrc
COPY --chown=${USERNAME}:${USERNAME} dot_zshrc /home/${USERNAME}/.zshrc
COPY --chown=${USERNAME}:${USERNAME} dot_tmux.conf /home/${USERNAME}/.tmux.conf
RUN curl -sS https://starship.rs/install.sh | sh -s -- -y \
    && echo 'eval "$(starship init bash)"' >> /home/${USERNAME}/.bashrc \
    && echo 'eval "$(starship init zsh)"' >> /home/${USERNAME}/.zshrc
COPY --chown=${USERNAME}:${USERNAME} starship.toml /home/${USERNAME}/.config/
USER root

# install just
RUN if [ "$TARGETARCH" = "amd64" ]; then \
        ITEMARCH="x86_64"; \
    elif [ "$TARGETARCH" = "arm64" ]; then \
        ITEMARCH="aarch64"; \
    else \
        exit 1; \
    fi; \
    curl -sL https://github.com/casey/just/releases/download/1.42.4/just-1.42.4-${ITEMARCH}-unknown-linux-musl.tar.gz | \
    tar xz --directory=/usr/local/bin just \
    && echo 'source <(just --completions bash)' >> /home/${USERNAME}/.bashrc \
    && echo 'source <(just --completions zsh)' >> /home/${USERNAME}/.zshrc

## Create locale
RUN locale-gen "en_US.UTF-8"

USER ${USERNAME}

