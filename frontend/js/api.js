const API_URL =
    "http://localhost:9000";

async function getApplications() {

    const response =
        await fetch(
            `${API_URL}/applications`
        );

    return response.json();
}