# Use a base image with Java 17 and Maven
FROM maven:3.8.5-openjdk-17

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file to download dependencies
COPY pom.xml .

# Download the dependencies (this step is cached if pom.xml doesn't change)
# We use 'go-offline' to download most dependencies, but 'package' or 'test' might still need more.
# Running a dependency:resolve is often enough for the basics.
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Default command to run tests
CMD ["mvn", "test"]
