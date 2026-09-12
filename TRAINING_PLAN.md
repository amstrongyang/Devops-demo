# Seven-day DevOps/CI/CD practice plan

Aim for 2–3 focused hours per day. The goal is not more Spring business code; it is a demonstrable story from commit to safe production operation. At the end of each day, save commands, screenshots, failures and fixes in a short learning log.

## Day 1 — Reproducible project and test baseline

**Hands-on goal:** run the supplied project, inspect its layers, execute all tests, call the API, and deliberately make one test fail before fixing it. Produce the JAR with `mvn clean package`.

**Knowledge:** Maven lifecycle; dependency and artifact; unit vs web-slice vs integration tests; H2 vs PostgreSQL; profiles; health checks; build reproducibility.

**Interview questions:**

- What stages would you put in a CI pipeline and why?
- What is the difference between unit, integration and end-to-end tests?
- Why must the same build be reproducible on a developer machine and a CI runner?

**English answer target:** “I start with a fast, reproducible Maven build. Unit tests give quick feedback, slice tests verify the HTTP contract, and Testcontainers checks PostgreSQL-specific behaviour. The pipeline should fail early before producing a deployable artifact.”

**Evidence:** passing test report, working health endpoint, API request/response, generated JAR.

## Day 2 — Docker and local production parity

**Hands-on goal:** understand every Dockerfile line; build/tag/run the image; use Compose with PostgreSQL; inspect logs, networks, layers and health; pass configuration through environment variables. Then improve the first Dockerfile with caching, pinned images and a health check if appropriate.

**Knowledge:** image vs container; layers and cache; multi-stage builds; non-root user; port exposure; volumes; container networking; immutable images; twelve-factor configuration.

**Interview questions:**

- How do you make a Java Docker image small and secure?
- Why does `localhost` inside the app container not reach PostgreSQL?
- What is the difference between `COPY`, `ADD`, `CMD` and `ENTRYPOINT`?

**English answer target:** “I use a multi-stage build so Maven stays out of the runtime image, run the process as a non-root user, externalise configuration, and connect services by Compose DNS name. I optimise layer order so dependency downloads are cached.”

**Evidence:** image size/history, successful Compose run, persisted PostgreSQL data, explanation of one container failure and fix.

## Day 3 — Git workflow and GitHub Actions CI

**Hands-on goal:** create a GitHub repository and small feature branch; add a pull-request workflow for checkout, Java setup with Maven cache, compile, tests and package; upload test reports/JAR; protect the main branch; observe a failed check and repair it.

**Knowledge:** trunk-based development vs GitFlow; PR checks; runner; trigger; job/step; cache vs artifact; least-privilege workflow permissions; secrets; commit SHA traceability.

**Interview questions:**

- Describe a CI pipeline you built.
- Cache and artifact: what is the difference?
- How do you prevent broken code from reaching main?
- How would you troubleshoot a pipeline that works locally but fails in CI?

**English answer target:** “On each pull request, GitHub Actions builds with Java 17, restores the Maven dependency cache, runs tests and publishes reports. Branch protection requires the check before merge. Artifacts are outputs we retain; caches only speed up future runs.”

**Evidence:** workflow YAML written by you, successful and failed runs, branch-protection screenshot, downloadable JAR tied to a commit.

## Day 4 — Quality gates, supply-chain checks and ECR

**Hands-on goal:** add SonarQube/SonarCloud analysis, OWASP Dependency-Check and Trivy scanning; decide which severity fails the build; generate an SBOM; configure AWS identity (prefer OIDC), create ECR, tag the image with commit SHA and push it only after CI passes.

**Knowledge:** SAST vs SCA vs container scanning; CVE, CVSS, false positive and allowlist; quality gate; SBOM; registry; mutable vs immutable tags; OIDC vs long-lived credentials.

**Interview questions:**

- Where do security scans belong in CI/CD?
- What do you do when a critical CVE has no immediate fix?
- How should a pipeline authenticate to AWS?
- Why is `latest` insufficient for deployment and rollback?

**English answer target:** “I shift checks left with code quality, dependency and image scanning. I publish only after the quality gate passes, authenticate to AWS using short-lived OIDC credentials, and tag the image with the immutable commit SHA for auditability and rollback.”

**Evidence:** scan reports and SBOM, documented threshold/exception decision, ECR image whose tag maps to a Git commit.

## Day 5 — Kubernetes deployment

**Hands-on goal:** use kind or minikube; write Deployment and Service manifests plus readiness/liveness/startup probes, resource requests/limits and rolling-update settings; inject configuration; deploy the ECR or local image; scale, inspect, break a probe and recover; perform a rollout and rollback.

**Knowledge:** cluster/node/pod; Deployment/ReplicaSet; Service and DNS; declarative desired state; probes; requests/limits; scheduling; rollout; namespace; image pull.

**Interview questions:**

- Deployment, ReplicaSet and Pod: how do they relate?
- Readiness vs liveness vs startup probe?
- Why set resource requests and limits?
- How do you diagnose `CrashLoopBackOff` or `ImagePullBackOff`?

**English answer target:** “The Deployment owns ReplicaSets, which maintain Pods. Readiness controls traffic, liveness restarts a stuck process, and startup protects slow initialisation. I debug from events and pod status, then logs, configuration, probes and resource pressure.”

**Evidence:** your own manifests, healthy pods and service call, rollout history, successful rollback, short troubleshooting record.

## Day 6 — OpenShift and multi-environment configuration

**Hands-on goal:** deploy to OpenShift Local or a sandbox; adapt Kubernetes manifests to OpenShift security constraints; expose a Route; separate dev/test/prod overlays with Kustomize or Helm; create ConfigMaps and Secrets without committing secret values; practise service-account/RBAC basics.

**Knowledge:** OpenShift projects, Routes, SCC and arbitrary UID; ConfigMap vs Secret; environment promotion; overlays/templates; RBAC; sealed/external secrets concepts.

**Interview questions:**

- How does OpenShift differ from Kubernetes?
- ConfigMap vs Secret, and how do you keep secrets out of Git?
- How do you promote the same application through environments?
- Why might an image run in Docker but fail on OpenShift?

**English answer target:** “OpenShift adds an enterprise platform layer including Routes and stricter security defaults. I build one immutable image and promote it across environments; only external configuration changes. Secret values come from a managed secret store, not Git.”

**Evidence:** Route URL, running workload under OpenShift constraints, dev/prod rendered-config diff with no secret leakage, RBAC explanation.

## Day 7 — Safe delivery, rollback, observability and interview rehearsal

**Hands-on goal:** choose and demonstrate rolling, blue/green or canary delivery; inject a bad release and roll back using an immutable tag; expose Actuator/Prometheus metrics and build a minimal dashboard/alert; trace commit → CI run → image → deployment; rehearse answers using your evidence.

**Knowledge:** deployment strategies and trade-offs; zero-downtime constraints; rollback vs roll-forward; logs/metrics/traces; SLI/SLO; alert quality; DORA metrics; incident learning.

**Interview questions:**

- Explain your end-to-end CI/CD design.
- Rolling, blue/green and canary: when would you choose each?
- How do you know a deployment is healthy, and what triggers rollback?
- Tell me about a deployment failure and your response.

**English answer target:** “A pull request passes automated tests, quality and security gates. The pipeline builds one immutable image, signs/tags it by commit, and promotes it through environments. Kubernetes performs a controlled rollout; probes and service metrics verify health. If error rate or latency breaches the threshold, I stop promotion and roll back to the previous immutable image, then preserve evidence for a blameless review.”

**Evidence:** deployment comparison table, failed-release timeline, rollback proof, dashboard/alert, a polished 2-minute architecture answer and three STAR stories.

## Final two-minute interview structure

1. **Context:** small Spring Boot order API, PostgreSQL, one-week production-path exercise.
2. **Commit:** feature branch, PR review and required CI checks.
3. **Verify:** layered tests, quality gate, SCA/image scanning and SBOM.
4. **Package:** multi-stage non-root image, immutable commit tag, ECR via OIDC.
5. **Configure:** one image promoted through environments; external ConfigMap/Secret values.
6. **Deploy:** Kubernetes/OpenShift probes, resources and controlled rollout.
7. **Operate:** health signals, monitoring, rollback and learning from failure.

Do not claim production experience you do not have. Say: “I built and operated this end-to-end in a production-like lab, and here is how I would apply the same controls in a team environment.”
