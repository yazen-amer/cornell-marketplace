"""Local SMTP capture for the demo profile. Never forwards mail to Cornell or elsewhere.

Run with: uv run --with aiosmtpd --no-project python scripts/demo_mailbox.py
"""
from aiosmtpd.controller import Controller
from email import policy
from email.parser import BytesParser
from threading import Event

class Mailbox:
    async def handle_DATA(self, server, session, envelope):
        message = BytesParser(policy=policy.default).parsebytes(envelope.content)
        print("Local demo mail to:", envelope.rcpt_tos, flush=True)
        print(message.get_body(preferencelist=("plain",)).get_content(), flush=True)
        return "250 Captured locally; not delivered externally"

if __name__ == "__main__":
    controller = Controller(Mailbox(), hostname="127.0.0.1", port=1025)
    controller.start()
    print("Demo mailbox listening on 127.0.0.1:1025. Ctrl+C to stop.", flush=True)
    try:
        Event().wait()
    except KeyboardInterrupt:
        pass
    finally:
        controller.stop()
