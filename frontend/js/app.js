async function refreshApplications() {

    const applications =
        await getApplications();

    renderApplications(
        applications
    );
}

async function init() {

    await refreshApplications();

    setInterval(
        refreshApplications,
        5000
    );

    setInterval(
        refreshLogs,
        1000
    );
}

init();