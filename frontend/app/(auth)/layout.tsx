import React from "react"
import Link from "next/link"
import Image from "next/image"
import { Leaf } from "lucide-react"
import { NoiseOverlay } from "@/components/noise-overlay"

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <>
      <NoiseOverlay />
      <div className="flex min-h-screen">
        {/* Left side - Food imagery */}
        <div className="relative hidden w-1/2 lg:block">
          {/* Full-bleed food image */}
          <Image
            src="/images/login-photo.jpg"
            alt="Healthy breakfast"
            fill
            className="object-cover"
            priority
          />
          {/* Gradient overlay for better logo visibility */}
          <div className="absolute inset-0 bg-gradient-to-b from-primary/80 via-primary/30 to-primary/60" />
          
          {/* Logo positioned over the image */}
          <div className="absolute left-12 top-12 z-10">
            <Link href="/" className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-foreground/20 backdrop-blur-sm">
                <Leaf className="h-6 w-6 text-primary-foreground" />
              </div>
              <span className="sr-only">BiteRight</span>
              <Image
                src="/biteright-logo-black.png"
                alt="BiteRight Logo"
                width={160}
                height={50}
                className="h-14 w-auto object-contain brightness-0 invert"
              />
            </Link>
          </div>

          {/* Tagline at bottom */}
          <div className="absolute bottom-12 left-12 right-12 z-10">
            <p className="text-xl font-medium text-primary-foreground drop-shadow-lg">
              Eat well. Live well. Feel amazing.
            </p>
          </div>
        </div>

        {/* Right side - Form */}
        <div className="flex w-full flex-col bg-background lg:w-1/2">
          <div className="flex flex-1 flex-col items-center justify-center px-4 py-12 sm:px-6 lg:px-12">
            <div className="mb-8 lg:hidden">
              <Link href="/" className="flex items-center gap-2">
                <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary">
                  <Leaf className="h-5 w-5 text-primary-foreground" />
                </div>
                <span className="sr-only">BiteRight</span>
                <Image
                  src="/biteright-logo-black.png"
                  alt="BiteRight Logo"
                  width={140}
                  height={50}
                  className="h-14 w-auto object-contain"
                />
              </Link>
            </div>
            {children}
          </div>
        </div>
      </div>
    </>
  )
}
