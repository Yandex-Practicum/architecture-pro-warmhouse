var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();

var telemetryData = new List<TelemetryData>();

app.MapPost("/api/telemetry", (TelemetryData data) =>
{
    data.Timestamp = DateTime.UtcNow;
    telemetryData.Add(data);
    return Results.Ok(data);
});

app.MapGet("/api/telemetry", () => Results.Ok(telemetryData));

app.Run();

public record TelemetryData
{
    public string DeviceId { get; set; }
    public string Type { get; set; }
    public double Value { get; set; }
    public DateTime Timestamp { get; set; }
}
