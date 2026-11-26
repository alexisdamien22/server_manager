const API_URL =
    "http://localhost:9000";

async function getApplications() {

    const response =
        await fetch(
            `${API_URL}/applications`
        );

    return response.json();
}

async function startApplication(
    id) {

    await fetch(
        `${API_URL}/applications/start?id=${id}`,
        {
            method: "POST"
        }
    );
}

async function stopApplication(
    id) {

    await fetch(
        `${API_URL}/applications/stop?id=${id}`,
        {
            method: "POST"
        }
    );
}

async function restartApplication(
    id) {

    await fetch(
        `${API_URL}/applications/restart?id=${id}`,
        {
            method: "POST"
        }
    );
}

async function getLogs(
    id) {

    const response =
        await fetch(
            `${API_URL}/applications/logs?id=${id}`
        );

    return response.json();
}