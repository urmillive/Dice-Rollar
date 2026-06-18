# Hosting the Privacy Policy on GitHub Pages

Run these commands from anywhere. The result is a public URL you can paste into the Play Console "Privacy policy" field.

```bash
mkdir -p ~/tmp/dicerollar-privacy && cd ~/tmp/dicerollar-privacy
cp "/Users/home/Projects/Mobile Apps/Dice-Rollar/store/PRIVACY-POLICY.md" index.md
cat > _config.yml <<'EOF'
title: Dice Rollar Privacy Policy
theme: jekyll-theme-cayman
EOF
git init -b main && git add . && git commit -m "feat: dice rollar privacy policy"
gh repo create dicerollar-privacy --public --source=. --remote=origin --push
gh api -X POST "repos/urmillive/dicerollar-privacy/pages" -f "source[branch]=main" -f "source[path]=/"
# Final URL: https://urmillive.github.io/dicerollar-privacy/
```

GitHub Pages can take 1-3 minutes to build the first time. Refresh the URL until the styled page loads.

Paste `https://urmillive.github.io/dicerollar-privacy/` into:
- Play Console -> App content -> Privacy policy
