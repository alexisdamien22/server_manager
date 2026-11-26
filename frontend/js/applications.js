function renderApplications(
    applications) {

    const container =
        document.getElementById(
            "applications"
        );

    container.innerHTML = "";

    applications.forEach(
        application => {

            const card =
                document.createElement(
                    "div"
                );

            card.className =
                "application-card";

            card.innerHTML = `
                <h3>
                    ${application.name}
                </h3>

                <p>
                    ${application.running
                    ? '<span class="running">Running</span>'
                    : '<span class="stopped">Stopped</span>'
                }
                </p>

                <div class="actions">

                    <button
                        ${application.running ? "disabled" : ""}
                        onclick="handleStart('${application.id}')">
                        Start
                    </button>

                    <button
                        ${application.running ? "" : "disabled"}
                        onclick="handleStop('${application.id}')">
                        Stop
                    </button>

                    <button
                        ${application.running ? "" : "disabled"}
                        onclick="handleRestart('${application.id}')">
                        Restart
                    </button>

                    <button
                        onclick="loadLogs('${application.id}')">
                        Logs
                    </button>

                </div>
            `;


            container.appendChild(
                card
            );
        }
    );
}

async function handleStart(
    id) {

    await startApplication(id);

    await refreshApplications();
}

async function handleStop(
    id) {

    await stopApplication(id);

    await refreshApplications();
}

async function handleRestart(
    id) {

    await restartApplication(id);

    await refreshApplications();
}