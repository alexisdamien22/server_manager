async function init() {

    const applications =
        await getApplications();

    renderApplications(
        applications
    );
}

init();