import AutomatedVerifyForm from "./verify-form"

export function generateStaticParams() {
  return [{ email: "email", token: "token" }]
}

export default async function AutomatedVerifyPage({ params }: { params: Promise<{ email: string, token: string }> }) {
  const { email, token } = await params

  return <AutomatedVerifyForm initialEmail={email} initialToken={token} />
}
