import asyncio
import websockets

async def hello(uri):
    async with websockets.connect(uri) as websocket:
        await websocket.send("Hello world!")
        async for message in websocket:
            print("msg: ", message)

asyncio.get_event_loop().run_until_complete(hello('ws://echo.websocket.org'))
#asyncio.get_event_loop().run_until_complete(hello('ws://192.168.188.43:8765'))  #python ws server
#asyncio.get_event_loop().run_until_complete(hello('ws://192.168.188.43:8080/ws/')) #actix ws server
