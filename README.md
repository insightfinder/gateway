# Gateway
The LLM Gateway service for InsightFinder AI.

# Usage
Our Gateway service can be accessible via the OpenAI Python SDK.
We just need to do the following changes during OpenAI SDK initialization:
1. Set the base_url to the InsightFinder Gateway service URL.
2. Set the api_key to the following format: "<IF_USERNAME>|<IF_LICENSE_KEY>". Example: `api_key="aiuser|abcd1234xxxxxxxxx"`
3. Set the model to "gateway".

```python
from openai import OpenAI

client = OpenAI(
    api_key="<IF_USERNAME>|<IF_LICENSE_KEY>",
    base_url="https://ai-stg.insightfinder.com"
)

completion = client.chat.completions.create(
    model="gateway",
    messages=[
        {"role": "system", "content": "You are a helpful assistant."},
        {"role": "user", "content": "Hello!"}
    ],
    stream=True
)

print(completion.choices[0].message)
```
