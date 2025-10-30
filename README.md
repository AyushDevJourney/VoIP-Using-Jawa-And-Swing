# 🔊 IoHT AUDIO COMM-LINK (UDP Real-Time Audio Stream)

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java%20(Swing%2FNet)-blue" alt="Java Language Badge">
  <img src="https://img.shields.io/badge/Protocol-UDP-orange" alt="UDP Protocol Badge">
  <img src="https://img.shields.io/badge/Encoding-u--Law%20(G.711)-red" alt="u-Law Encoding Badge">
</p>

## 🚀 Project Overview

The **IoHT AUDIO COMM-LINK** is a simple, real-time, one-way audio streaming application built in Java. It utilizes the **User Datagram Protocol (UDP)** for low-latency transmission and employs the **µ-Law (G.711)** audio compression algorithm to reduce bandwidth requirements, simulating a cyberpunk-style "Internet of Hacked Things" communication link.

The project consists of two distinct components, each with a custom, neon-themed Swing GUI:

1.  **`AudioSender.java`**: Captures microphone data, compresses it using u-Law encoding, and streams it over UDP.
2.  **`AudioReceiver.java`**: Listens for the UDP stream, decodes the u-Law data, and plays the audio through the speakers.

---

## 🛠 Prerequisites

* **Java Development Kit (JDK) 8 or newer** (required for compiling and running).
* A **microphone** and **speakers** (or a headset) for testing the full communication link.
* **Two terminal windows** or command prompts (one for the Sender, one for the Receiver).

---

## ⚡ Quick Start: Interactive Guide

Follow these steps to quickly compile and run the Sender and Receiver on the same machine (using `127.0.0.1`).

### 1. Compile the Source Code

Execute the following command in the root directory where `AudioSender.java` and `AudioReceiver.java` are located:

```bash
javac AudioSender.java AudioReceiver.java
