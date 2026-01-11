var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

app.MapGet("/temperature", (string? location, string? sensorId) =>
{
    var resolvedLocation = location ?? string.Empty;
    var resolvedSensorId = sensorId ?? string.Empty;

    if (string.IsNullOrEmpty(resolvedLocation))
    {
        resolvedLocation = resolvedSensorId switch
        {
            "1" => "Living Room",
            "2" => "Bedroom",
            "3" => "Kitchen",
            _ => "Unknown"
        };
    }

    if (string.IsNullOrEmpty(resolvedSensorId))
    {
        resolvedSensorId = resolvedLocation switch
        {
            "Living Room" => "1",
            "Bedroom" => "2",
            "Kitchen" => "3",
            _ => "0"
        };
    }

    var random = new Random();
    var temperature = Math.Round(random.NextDouble() * 30, 1);

    return Results.Ok(new
    {
        sensorId = resolvedSensorId,
        location = resolvedLocation,
        temperature,
        unit = "°C",
        timestamp = DateTime.UtcNow
    });
});

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();

app.MapControllers();

app.Run();
