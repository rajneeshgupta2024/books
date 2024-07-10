# KEDA (Kubernetes Event-Driven Autoscaling) 
KEDA is a component that enables Kubernetes-based applications to scale based on external events. It acts as a bridge between Kubernetes and external event sources, such as messaging queues, databases, or custom metrics, allowing Kubernetes to automatically scale applications up or down in response to these events.

## Significance of KEDA

**1. Event-Driven Scaling:** KEDA allows applications to scale based on external event triggers, making it ideal for workloads that have variable demand.<br>
**2. Cost Efficiency:** By scaling applications up or down based on actual usage, KEDA helps optimize resource usage, potentially lowering costs.<br>
**3. Integration with Kubernetes:** KEDA integrates seamlessly with Kubernetes, leveraging native Kubernetes features like Horizontal Pod Autoscaler (HPA).<br>
**4. Supports Various Event Sources:** KEDA supports a wide range of event sources, including Azure Service Bus, Apache Kafka, RabbitMQ, Prometheus, AWS SQS, and many others.<br>

## When to Use KEDA
-  When you have applications that need to scale based on external events.
-  When you want to optimize resource usage and costs by scaling applications dynamically.
-  When your application workload is variable and dependent on external systems.

## Example: Scaling a Kubernetes Deployment Based on Azure Queue Storage Messages
Let's go through a step-by-step example of how to use KEDA to scale a Kubernetes deployment based on messages in an Azure Queue Storage.

**Step 1.** Install KEDA
1.  Add the KEDA Helm repository
```sh
helm repo add kedacore https://kedacore.github.io/charts
helm repo update
```
2. Install KEDA
```sh
helm install keda kedacore/keda
```

**Step 2.** Create an Azure Queue Storage and Get Connection String
1. Create an Azure Storage Account and a Queue Storage within it.
2. Obtain the connection string for the storage account from the Azure portal.

**Step 3.** Deploy an Application to Kubernetes
Lets do simple application that processes messages from the Azure queue - 
1. Create a deployment file (deployment.yaml):
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: queue-consumer
spec:
  replicas: 1
  selector:
    matchLabels:
      app: queue-consumer
  template:
    metadata:
      labels:
        app: queue-consumer
    spec:
      containers:
      - name: queue-consumer
        image: your-queue-consumer-image
        env:
        - name: AZURE_STORAGE_CONNECTION_STRING
          valueFrom:
            secretKeyRef:
              name: azure-storage-secret
              key: connection-string
```
2. Create a secret to store the Azure Storage connection string
```sh
kubectl create secret generic azure-storage-secret --from-literal=connection-string=<your-azure-storage-connection-string>
```
3. Apply the deployment
```sh
kubectl apply -f deployment.yaml
```
**Step 4.** Create a KEDA ScaledObject
1. Create a ScaledObject file (scaledobject.yaml) -
```yaml
apiVersion: keda.sh/v1alpha1
kind: ScaledObject
metadata:
  name: azure-queue-scaler
spec:
  scaleTargetRef:
    name: queue-consumer
  minReplicaCount: 1
  maxReplicaCount: 10
  triggers:
  - type: azure-queue
    metadata:
      connectionFromEnv: AZURE_STORAGE_CONNECTION_STRING
      queueName: your-queue-name
      queueLength: "5"
```
2. Apply the scaled object -
```sh
kubectl apply -f scaledobject.yaml
```

**Step 5.** Monitor Scaling
KEDA will monitor the specified Azure Queue and automatically scale the queue-consumer deployment based on the number of messages in the queue. If the number of messages exceeds the threshold (in this case, 5), KEDA will scale up the deployment. When the number of messages decreases, KEDA will scale down the deployment.

## Conclusion
KEDA provides a powerful mechanism for event-driven autoscaling in Kubernetes, enabling efficient resource utilization and cost savings. By integrating with various external event sources, KEDA allows you to build responsive and scalable applications.
