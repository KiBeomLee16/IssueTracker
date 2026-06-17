const apiBase = window.location.origin && window.location.origin !== "null"
  ? window.location.origin
  : "http://localhost:8080";

const state = {
  token: localStorage.getItem("demo.accessToken") || "",
  userId: localStorage.getItem("demo.userId") || "",
  projects: [],
  selectedProjectId: null,
  issues: [],
  selectedIssueId: null
};

const els = {
  sessionStatus: document.querySelector("#sessionStatus"),
  messageBox: document.querySelector("#messageBox"),
  loginForm: document.querySelector("#loginForm"),
  userIdInput: document.querySelector("#userIdInput"),
  passwordInput: document.querySelector("#passwordInput"),
  logoutButton: document.querySelector("#logoutButton"),
  refreshProjectsButton: document.querySelector("#refreshProjectsButton"),
  refreshIssuesButton: document.querySelector("#refreshIssuesButton"),
  projectForm: document.querySelector("#projectForm"),
  projectNameInput: document.querySelector("#projectNameInput"),
  projectList: document.querySelector("#projectList"),
  selectedProjectLabel: document.querySelector("#selectedProjectLabel"),
  statsGrid: document.querySelector("#statsGrid"),
  issueForm: document.querySelector("#issueForm"),
  issueTitleInput: document.querySelector("#issueTitleInput"),
  issuePriorityInput: document.querySelector("#issuePriorityInput"),
  todoIssues: document.querySelector("#todoIssues"),
  progressIssues: document.querySelector("#progressIssues"),
  doneIssues: document.querySelector("#doneIssues"),
  selectedIssueLabel: document.querySelector("#selectedIssueLabel"),
  issueDetail: document.querySelector("#issueDetail"),
  commentForm: document.querySelector("#commentForm"),
  commentInput: document.querySelector("#commentInput"),
  commentList: document.querySelector("#commentList"),
  historyList: document.querySelector("#historyList")
};

function setMessage(message, type = "") {
  els.messageBox.textContent = message;
  els.messageBox.className = `message-box ${type}`.trim();
}

function authHeaders() {
  return state.token ? { Authorization: `Bearer ${state.token}` } : {};
}

async function api(path, options = {}) {
  const headers = {
    Accept: "application/json",
    ...authHeaders(),
    ...(options.body ? { "Content-Type": "application/json" } : {})
  };

  const response = await fetch(`${apiBase}${path}`, {
    ...options,
    headers: { ...headers, ...(options.headers || {}) }
  });

  let payload = null;
  try {
    payload = await response.json();
  } catch (error) {
    payload = null;
  }

  if (!response.ok || payload?.success === false) {
    throw new Error(payload?.message || `Request failed with ${response.status}`);
  }

  return payload?.data ?? null;
}

function renderSession() {
  if (state.token) {
    els.sessionStatus.textContent = `Signed in: ${state.userId}`;
    els.sessionStatus.classList.add("online");
  } else {
    els.sessionStatus.textContent = "Signed out";
    els.sessionStatus.classList.remove("online");
  }
}

function getSelectedProject() {
  return state.projects.find((project) => project.id === state.selectedProjectId) || null;
}

function getSelectedIssue() {
  return state.issues.find((issue) => issue.id === state.selectedIssueId) || null;
}

function renderProjects() {
  els.projectList.replaceChildren();

  state.projects.forEach((project) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = `project-item ${project.id === state.selectedProjectId ? "active" : ""}`.trim();
    button.innerHTML = `
      <span class="item-title">${escapeHtml(project.name)}</span>
      <span class="item-meta">#${project.id} / ${project.status || "ACTIVE"}</span>
    `;
    button.addEventListener("click", () => selectProject(project.id));
    els.projectList.append(button);
  });
}

function renderStats(stats) {
  els.statsGrid.replaceChildren();
  if (!stats) {
    els.selectedProjectLabel.textContent = "No project";
    return;
  }

  els.selectedProjectLabel.textContent = stats.projectName || `Project #${stats.projectId}`;
  [
    ["Total", stats.totalIssues],
    ["TODO", stats.todoCount],
    ["In progress", stats.inProgressCount],
    ["Done", stats.doneCount],
    ["Comments", stats.totalComments],
    ["Completion", `${Math.round(stats.completionRate || 0)}%`]
  ].forEach(([label, value]) => {
    const item = document.createElement("div");
    item.className = "stat";
    item.innerHTML = `<strong>${value}</strong><span>${label}</span>`;
    els.statsGrid.append(item);
  });
}

function renderIssues() {
  els.todoIssues.replaceChildren();
  els.progressIssues.replaceChildren();
  els.doneIssues.replaceChildren();

  const targets = {
    TODO: els.todoIssues,
    IN_PROGRESS: els.progressIssues,
    DONE: els.doneIssues
  };

  state.issues.forEach((issue) => {
    const card = document.createElement("article");
    card.className = `issue-card ${issue.id === state.selectedIssueId ? "active" : ""}`.trim();
    card.innerHTML = `
      <div class="item-title">${escapeHtml(issue.title)}</div>
      <div class="item-meta">#${issue.id} / ${issue.assignee?.userId || "unassigned"}</div>
      <span class="priority ${issue.priority || "MEDIUM"}">${issue.priority || "MEDIUM"}</span>
      <div class="issue-actions">
        <button type="button" data-select="${issue.id}">Detail</button>
        <button type="button" data-status="TODO">TODO</button>
        <button type="button" data-status="IN_PROGRESS">Doing</button>
        <button type="button" data-status="DONE">Done</button>
      </div>
    `;

    card.querySelector("[data-select]").addEventListener("click", () => selectIssue(issue.id));
    card.querySelectorAll("[data-status]").forEach((button) => {
      button.addEventListener("click", () => updateIssueStatus(issue.id, button.dataset.status));
    });

    (targets[issue.status] || targets.TODO).append(card);
  });
}

async function renderIssueDetail() {
  const issue = getSelectedIssue();
  els.commentList.replaceChildren();
  els.historyList.replaceChildren();

  if (!issue) {
    els.selectedIssueLabel.textContent = "No issue";
    els.issueDetail.className = "detail-empty";
    els.issueDetail.textContent = "Select an issue";
    return;
  }

  els.selectedIssueLabel.textContent = `#${issue.id}`;
  els.issueDetail.className = "detail-block";
  els.issueDetail.innerHTML = `
    <div class="item-title">${escapeHtml(issue.title)}</div>
    <div class="item-meta">${issue.status} / ${issue.priority || "MEDIUM"}</div>
    <p>${escapeHtml(issue.description || "No description")}</p>
  `;

  await Promise.all([loadComments(issue.id), loadHistory(issue.id)]);
}

async function login(userId, password) {
  const data = await api("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ userId, password })
  });

  state.token = data.accessToken;
  state.userId = userId;
  localStorage.setItem("demo.accessToken", state.token);
  localStorage.setItem("demo.userId", state.userId);
  renderSession();
  setMessage("Login succeeded", "success");
  await loadProjects();
}

function logout() {
  state.token = "";
  state.userId = "";
  state.projects = [];
  state.issues = [];
  state.selectedProjectId = null;
  state.selectedIssueId = null;
  localStorage.removeItem("demo.accessToken");
  localStorage.removeItem("demo.userId");
  renderSession();
  renderProjects();
  renderStats(null);
  renderIssues();
  renderIssueDetail();
  setMessage("Signed out");
}

async function loadProjects() {
  if (!state.token) {
    setMessage("Login required", "error");
    return;
  }

  state.projects = await api("/api/projects");
  if (!state.selectedProjectId && state.projects.length > 0) {
    state.selectedProjectId = state.projects[0].id;
  }
  renderProjects();

  if (state.selectedProjectId) {
    await selectProject(state.selectedProjectId);
  } else {
    renderStats(null);
    renderIssues();
  }
}

async function selectProject(projectId) {
  state.selectedProjectId = Number(projectId);
  state.selectedIssueId = null;
  renderProjects();

  const [stats, issues] = await Promise.all([
    api(`/api/projects/${state.selectedProjectId}/stats`),
    api(`/api/projects/${state.selectedProjectId}/issues`)
  ]);

  state.issues = issues || [];
  renderStats(stats);
  renderIssues();
  await renderIssueDetail();
}

async function createProject(name) {
  await api("/api/projects", {
    method: "POST",
    body: JSON.stringify({
      name,
      description: "Created from the demo console.",
      status: "ACTIVE"
    })
  });
  setMessage("Project created", "success");
  els.projectNameInput.value = "";
  await loadProjects();
}

async function createIssue(title, priority) {
  if (!state.selectedProjectId) {
    setMessage("Select a project first", "error");
    return;
  }

  const dueDate = new Date();
  dueDate.setDate(dueDate.getDate() + 7);

  await api(`/api/projects/${state.selectedProjectId}/issues`, {
    method: "POST",
    body: JSON.stringify({
      title,
      description: "Created from the demo console.",
      status: "TODO",
      priority,
      dueDate: dueDate.toISOString().slice(0, 10)
    })
  });

  setMessage("Issue created", "success");
  els.issueTitleInput.value = "";
  await selectProject(state.selectedProjectId);
}

async function updateIssueStatus(issueId, status) {
  await api(`/api/issues/${issueId}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status })
  });
  setMessage(`Issue moved to ${status}`, "success");
  await selectProject(state.selectedProjectId);
  state.selectedIssueId = Number(issueId);
  renderIssues();
  await renderIssueDetail();
}

async function selectIssue(issueId) {
  state.selectedIssueId = Number(issueId);
  renderIssues();
  await renderIssueDetail();
}

async function createComment(content) {
  if (!state.selectedIssueId) {
    setMessage("Select an issue first", "error");
    return;
  }

  await api(`/api/issues/${state.selectedIssueId}/comments`, {
    method: "POST",
    body: JSON.stringify({ content })
  });

  setMessage("Comment added", "success");
  els.commentInput.value = "";
  await renderIssueDetail();
}

async function loadComments(issueId) {
  const comments = await api(`/api/issues/${issueId}/comments`);
  els.commentList.replaceChildren();
  comments.forEach((comment) => {
    const item = document.createElement("div");
    item.className = "comment-item";
    item.innerHTML = `
      <div>${escapeHtml(comment.content)}</div>
      <div class="item-meta">#${comment.id}</div>
    `;
    els.commentList.append(item);
  });
}

async function loadHistory(issueId) {
  const histories = await api(`/api/issues/${issueId}/histories`);
  els.historyList.replaceChildren();
  histories.slice().reverse().forEach((history) => {
    const item = document.createElement("div");
    item.className = "history-item";
    item.innerHTML = `
      <div>${escapeHtml(history.action)} ${escapeHtml(history.fieldName || "")}</div>
      <div class="item-meta">${escapeHtml(history.actorUserId || "system")}</div>
    `;
    els.historyList.append(item);
  });
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function wireEvents() {
  els.loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
      await login(els.userIdInput.value.trim(), els.passwordInput.value);
    } catch (error) {
      setMessage(error.message, "error");
    }
  });

  document.querySelectorAll("[data-login]").forEach((button) => {
    button.addEventListener("click", async () => {
      els.userIdInput.value = button.dataset.login;
      els.passwordInput.value = "password";
      try {
        await login(button.dataset.login, "password");
      } catch (error) {
        setMessage(error.message, "error");
      }
    });
  });

  els.logoutButton.addEventListener("click", logout);
  els.refreshProjectsButton.addEventListener("click", () => loadProjects().catch((error) => setMessage(error.message, "error")));
  els.refreshIssuesButton.addEventListener("click", () => {
    if (state.selectedProjectId) {
      selectProject(state.selectedProjectId).catch((error) => setMessage(error.message, "error"));
    }
  });

  els.projectForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const name = els.projectNameInput.value.trim();
    if (!name) return;
    try {
      await createProject(name);
    } catch (error) {
      setMessage(error.message, "error");
    }
  });

  els.issueForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const title = els.issueTitleInput.value.trim();
    if (!title) return;
    try {
      await createIssue(title, els.issuePriorityInput.value);
    } catch (error) {
      setMessage(error.message, "error");
    }
  });

  els.commentForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const content = els.commentInput.value.trim();
    if (!content) return;
    try {
      await createComment(content);
    } catch (error) {
      setMessage(error.message, "error");
    }
  });
}

wireEvents();
renderSession();
renderStats(null);
renderIssues();
renderIssueDetail();

if (state.token) {
  loadProjects().catch((error) => setMessage(error.message, "error"));
}
