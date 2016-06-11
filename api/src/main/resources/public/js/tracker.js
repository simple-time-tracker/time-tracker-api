function startButtonListener(baseUrl) {
    $('#start').click(function () {
        var projectId = $('#project').val();
        var descriptionParam = $('#description').serialize();

        $.ajax({
            method: 'POST',
            url: baseUrl + 'api/entries/start/' + projectId + "?" + descriptionParam,
            success: function () {
                init(baseUrl);
            }
        })
    });
}

function stopButtonListener(baseurl) {
    $('#stop').click(function () {
        $.ajax({
            method: 'POST',
            url: baseurl + 'api/entries/stop',
            success: function () {
                $('#start').prop("disabled", false);
                $('#start-tracking-fields').show();
                $('#current-task').hide();
                $('#stop').prop("disabled", true);
            }
        })
    });
}

function initSelectProject(baseUrl) {
    var projectDropdown = $("#project");
    $.ajax({
        type: "GET",
        url: baseUrl + "/api/projects",
        success: function (data) {
            if (data.length) {
                $.each(data, function (i, d) {
                    projectDropdown.append('<option value="' + d.id + '">' + d.name + '</option>');
                });
            }

        }
    });
}

function init(baseUrl) {
    $('#stop').prop("disabled", true);

    $.ajax({
        url: baseUrl + 'api/entries/current',
        success: function (data, textStatus, xhr) {
            if (xhr.status == '200' && data) {
                var entry = data;


                $('#start').prop("disabled", true);
                $('#stop').prop("disabled", false);
                $('#start-tracking-fields').hide();

                var currentTask = $('#current-task');
                currentTask.show();

                currentTask.empty();
                var message = "Currently working on " + entry.project.name + " project. Current task: "
                    + entry.description + ' . Start date ' + (new Date(entry.startDate).toLocaleString());

                var node = document.createElement("p");
                var text = document.createTextNode(message);
                node.appendChild(text);

                currentTask.append(node);
            }
        }
    });
}