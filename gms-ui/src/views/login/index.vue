<template>
  <div class="login-page">
    <div class="login-page__visual" aria-hidden="true">
      <div class="login-page__aurora" />
      <div class="login-page__stars" />
      <div class="login-page__brand">
        <img class="login-page__logo" alt="BeiDou" src="/src/assets/logo.png" />
        <div class="login-page__brand-text">
          <span class="login-page__name">{{ $t('title') }}</span>
          <span class="login-page__tagline">{{
            $t('login.brand.tagline')
          }}</span>
        </div>
      </div>
      <p class="login-page__visual-copy">{{ $t('login.brand.copy') }}</p>
    </div>
    <div class="login-page__panel">
      <div class="login-page__panel-inner">
        <LoginForm />
      </div>
      <div class="login-page__footer">
        <Footer />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import Footer from '@/components/footer/index.vue';
  import LoginForm from './components/login-form.vue';
</script>

<style lang="less" scoped>
  .login-page {
    display: grid;
    grid-template-columns: minmax(0, 1.15fr) minmax(360px, 0.85fr);
    min-height: 100vh;
    background: var(--bd-surface);

    &__visual {
      position: relative;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      overflow: hidden;
      padding: 40px 48px 48px;
      color: #e8eef5;
      background: linear-gradient(
        145deg,
        #0a1628 0%,
        #12324a 48%,
        #1b6b93 100%
      );
    }

    &__aurora {
      position: absolute;
      inset: -20% -10% auto auto;
      width: 70%;
      height: 70%;
      background: radial-gradient(
        circle at 30% 40%,
        rgba(56, 189, 248, 0.35),
        transparent 55%
      );
      filter: blur(8px);
      pointer-events: none;
    }

    &__stars {
      position: absolute;
      inset: 0;
      background-image: radial-gradient(
          1.5px 1.5px at 12% 18%,
          rgba(255, 255, 255, 0.75),
          transparent
        ),
        radial-gradient(
          1px 1px at 28% 62%,
          rgba(255, 255, 255, 0.55),
          transparent
        ),
        radial-gradient(
          1.5px 1.5px at 64% 24%,
          rgba(201, 168, 76, 0.8),
          transparent
        ),
        radial-gradient(
          1px 1px at 78% 70%,
          rgba(255, 255, 255, 0.5),
          transparent
        ),
        radial-gradient(
          1.5px 1.5px at 88% 36%,
          rgba(255, 255, 255, 0.7),
          transparent
        );
      opacity: 0.85;
      pointer-events: none;
    }

    &__brand {
      position: relative;
      z-index: 1;
      display: flex;
      align-items: center;
      gap: 14px;
    }

    &__logo {
      width: 52px;
      height: 52px;
      object-fit: contain;
      filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.25));
    }

    &__brand-text {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    &__name {
      font-weight: 700;
      font-size: 22px;
      font-family: var(--bd-font-display);
      letter-spacing: -0.02em;
    }

    &__tagline {
      color: rgba(232, 238, 245, 0.72);
      font-size: 13px;
    }

    &__visual-copy {
      position: relative;
      z-index: 1;
      max-width: 420px;
      margin: 0;
      color: rgba(232, 238, 245, 0.82);
      font-size: 28px;
      font-weight: 600;
      font-family: var(--bd-font-display);
      line-height: 1.35;
      letter-spacing: -0.03em;
    }

    &__panel {
      position: relative;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 48px 32px 64px;
      background: var(--bd-surface-elevated);
    }

    &__panel-inner {
      width: 100%;
      max-width: 380px;
      padding: 36px 32px;
      background: var(--bd-surface-elevated);
      border: 1px solid var(--bd-border);
      border-radius: var(--bd-radius-xl);
      box-shadow: var(--bd-shadow-lg);
    }

    &__footer {
      position: absolute;
      right: 0;
      bottom: 0;
      left: 0;
    }
  }

  @media (max-width: 960px) {
    .login-page {
      grid-template-columns: 1fr;

      &__visual {
        min-height: 220px;
        padding: 28px 24px;
      }

      &__visual-copy {
        display: none;
      }

      &__panel {
        padding: 32px 20px 72px;
      }

      &__panel-inner {
        padding: 28px 22px;
        border: none;
        box-shadow: none;
      }
    }
  }
</style>
