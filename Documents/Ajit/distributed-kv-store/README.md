# Distributed Key-Value Store

A distributed key–value store implemented in **Java + Spring Boot**, built to understand
core distributed systems concepts such as partitioning, replication, and request routing.

This project was built incrementally to mirror how real distributed systems evolve.

---

## 🧠 High-Level Design

- Multiple independent nodes (separate JVMs)
- No shared memory between nodes
- Clients can read/write via **any node**
- Data is routed internally to the correct owner node

---

## ⚙️ Architecture Overview

### Partitioning
- Keys are deterministically assigned to a single **owner node**
- Ownership is computed using hashing
- All nodes independently agree on key ownership (no coordination)

### Replication
- Leader-based replication (Replication Factor = 2)
- Each key is stored on:
  - 1 owner node
  - 1 replica node
- Replica writes are **terminal** and do not trigger further replication

### Request Routing
- **PUT** requests:
  - Non-owner nodes forward writes to the owner
  - Owner writes locally and replicates to one replica
- **GET** requests:
  - If node is owner → read locally
  - Otherwise → forward to owner transparently

---

## 🧪 Failure Behavior

- If a **non-owner node fails** → system continues normally
- If the **owner node fails**:
  - Data still exists on the replica
  - Reads currently route to owner only (intentional design choice)
- In-memory storage (no persistence yet)

---

## 🧩 Key Distributed Systems Concepts Implemented

- Deterministic partitioning
- Leader-based replication
- Terminal replica writes
- Identity vs location separation (nodeId vs URL)
- Inter-node communication using HTTP
- Debugged real-world replication storm and identity consistency issues

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- Maven
- WebClient (for inter-node communication)

---

## ▶️ Running the Project

Run three instances with different profiles:

```bash
-Dspring.profiles.active=node1
-Dspring.profiles.active=node2
-Dspring.profiles.active=node3
