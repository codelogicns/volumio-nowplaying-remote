# Volumio NowPlaying Remote  🎶

[![Build](https://img.shields.io/badge/build-passing-brightgreen)](#)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-11%2B-orange)](#)
[![GraalVM](https://img.shields.io/badge/native--build-GraalVM-lightgrey)](#)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](#)

A lightweight command-line tool to remotely control the **Now Playing view** of your Volumio instance using WebSocket. It can run as a Java JAR, native executable (via GraalVM), or Docker container.
Why? to use with [volumio ir_controller plugin](https://github.com/volumio/volumio-plugins-sources/tree/master/ir_controller)
#### lircrc
```bash 
begin
prog = irexec
button = CD3
config = /data/INTERNAL/ir_controller/configurations/aiwa-rc-zas02/volumio-nowplaying-remote artist
end
```

---

## 🚀 Features

- Connects to Volumio via WebSocket (`http://127.0.0.1:3000` by default)
- Switches between Now Playing views: `song`, `artist`, `album`, `lyrics`
- JSON-style output for easy parsing
- Build as native binary using **GraalVM**
- Includes **Dockerfile** for containerized deployment
- Simple CLI for automation or scripting

---

## 📦 Supported Views

| Argument | Volumio View String |
|-----------|--------------------|
| `song`    | `nowPlaying.infoView` |
| `artist`  | `nowPlaying.infoView.artist` |
| `album`   | `nowPlaying.infoView.album` |
| `lyrics`  | `nowPlaying.infoView.lyrics` |

If an invalid argument is passed, the app outputs an error in JSON.

---

## 🧰 Prerequisites

- **Java 11+** (for JAR build)
- **Maven Wrapper** (included — no global install required)
- **GraalVM** (for native builds)
- **Docker** (optional)

---

## 🔧 Installation & Setup

### Clone the repository
```bash
git clone https://github.com/codelogicns/volumio-nowplaying-remote.git
cd volumio-nowplaying-remote
```

### Use Maven Wrapper (no Maven installation needed)
```bash
./mvnw wrapper:wrapper
./mvnw clean package
```
This builds the standard Java JAR in `target/volumio-nowplaying-remote.jar`.

---

## ⚙️ Native Build with GraalVM

1. **Install GraalVM** (e.g., via SDKMAN):
   ```bash
   sdk install java 21.0.x-graal
   sdk use java 21.0.x-graal
   gu install native-image
   ```

2. **Run the build script:**
   ```bash
   ./build-native.sh
   ```

3. The native executable will be generated in the `target/` directory.

---

## 🐳 Docker Build & Run

### Build image
```bash
docker build -f Dockerfile.native -t volumio-nowplaying-remote:latest .
```

### Run container
```bash
docker run --rm volumio-nowplaying-remote:latest <song|artist|album|lyrics>
```

---

## 💻 Usage

### Run via JAR
```bash
java -jar target/volumio-nowplaying-remote.jar <song|artist|album|lyrics>
```

### Run via Native Binary
```bash
./volumio-nowplaying-remote <song|artist|album|lyrics>
```

### Example Output
```json
{"status": "connected"}
{"action": "change_view", "target": "nowPlaying.infoView.artist"}
```

---

## 📡 WebSocket Request Payloads

When the tool connects, it sends a WebSocket message to Volumio:

```json
{
  "endpoint": "user_interface/now_playing",
  "method": "configSaveStartupOptions",
  "data": {
    "activeScreen": "nowPlaying.infoView.artist",
    "activateIdleScreen": false
  }
}
```
**Refresh Settings:**
```json
{
  "endpoint": "user_interface/now_playing",
  "method": "broadcastRefresh",
  "data": { }
}
```

**Invalid Input Example:**
```json
{"error": "Unknown view: badtype"}
```

**Connection Error Example:**
```json
{"error": "Failed to connect"}
```

---

## ⚙️ Configuration

- Default host: `http://127.0.0.1:3000`
- Update the constant `VOLHost` in `VolumioRemote.java` to change the Volumio server address.

---

## 🧪 Example Workflow

```bash
# Build
./mvnw clean package

# Run (artist view)
java -jar target/volumio-nowplaying-remote.jar artist

# Or using Docker
./build-native.sh && docker build -f Dockerfile.native -t volumio-nowplaying-remote .
docker run --rm volumio-nowplaying-remote song
```

---

## 🤝 Contributing

Contributions are welcome! Feel free to fork, submit issues, or open pull requests.

---

## 📄 License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) for details.

---

**Maintainer:** [codelogicns](https://github.com/codelogicns)

