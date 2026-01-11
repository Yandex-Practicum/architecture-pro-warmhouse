var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

  app.UseSwagger();
  app.UseSwaggerUI();


var devices = new List<Device>();

app.MapPost("/api/devices", (Device device) =>
{
    device.Id = Guid.NewGuid();
    device.Status = "inactive";
    devices.Add(device);
    return Results.Created($"/api/devices/{device.Id}", device);
});

app.MapGet("/api/devices/{id:guid}", (Guid id) =>
{
    var device = devices.FirstOrDefault(d => d.Id == id);
    return device is not null ? Results.Ok(device) : Results.NotFound();
});

app.Run();

public record Device
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    public string Type { get; set; }
    public string Status { get; set; }
}

