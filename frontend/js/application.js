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
                    ? '<span class="running">🟢 Running</span>'
                    : '<span class="stopped">🔴 Stopped</span>'
                }
                </p>
            `;

            container.appendChild(
                card
            );
        }
    );
}