extern crate actix;
extern crate actix_web;

use actix::*;
use actix_web::*;
use actix_web::ws::WsWriter;

struct Ws;

impl Actor for Ws {
    type Context = ws::WebsocketContext<Self>;

    fn started(&mut self, ctx: & mut <Self as Actor>::Context) {
        println!("ws started");
    }

    fn stopped(&mut self, ctx: & mut <Self as Actor>::Context) {
        println!("ws stopped");
    }
}

impl StreamHandler<ws::Message, ws::ProtocolError> for Ws {
    fn handle(&mut self, msg: ws::Message, ctx: & mut Self::Context) {
        println!("handle: {:?}", msg);
        match msg {
            ws::Message::Text(text) => {
                ctx.send_text(text)
            }
            _ => (),
        }
    }

    fn started(&mut self, ctx: &mut Self::Context) {
        println!("stream started");
    }

    fn error(&mut self, err: ws::ProtocolError, ctx: &mut Self::Context) -> Running {
        println!("stream error: {:?}", err);
        Running::Stop
    }

    fn finished(&mut self, ctx: &mut Self::Context) {
        println!("stream finished");
    }
}

fn ws_index(r: &HttpRequest) -> Result<HttpResponse, Error> {
    ws::start(r, Ws)
}

fn main() {
    println!("start server ... ");

    let sys = actix::System::new("flutter websocket test");

    server::new(move || {
        App::new()
            .resource("/ws/", |r| r.method(http::Method::GET).f(ws_index))
    })
        .bind("0.0.0.0:8080")
        .unwrap()
        .start();

    println!("server started: 0.0.0.0:8080");
    let _ = sys.run();
}
