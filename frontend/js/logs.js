let selectedApplicationId =
    null;

async function loadLogs(
    id) {

    selectedApplicationId = id;

    const data =
        await getLogs(id);

    document.getElementById(
        "logs-title"
    ).textContent =
        `Logs - ${id}`;

    renderLogs(data);
}

function renderLogs(
    data) {

    const container =
        document.getElementById(
            "logs"
        );

    if (!data.logs ||
        data.logs.length === 0) {

        container.textContent =
            "No logs";

        return;
    }

    container.innerHTML =
        data.logs
            .map(
                log =>
                    `[${new Date(
                        log.timestamp
                    ).toLocaleTimeString()}] ${log.message}`
            )
            .join("<br>");
    container.scrollTop =
        container.scrollHeight;
}

async function refreshLogs() {

    if (!selectedApplicationId) {
        return;
    }

    await loadLogs(
        selectedApplicationId
    );
}